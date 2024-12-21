package com.bikestore.controller.API;

import java.io.Console;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.IntStream;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bikestore.config.VNPayConfig;
import com.bikestore.model.Account;
import com.bikestore.model.Address;
import com.bikestore.model.CartDetail;
import com.bikestore.model.OrderDetail;
import com.bikestore.model.Orders;
import com.bikestore.model.Payments;
import com.bikestore.model.ProductDetail;
import com.bikestore.model.Status;
import com.bikestore.model.Transactions;
import com.bikestore.repository.AccountRepository;
import com.bikestore.repository.AddressRepository;
import com.bikestore.repository.CartDetailRepository;
import com.bikestore.repository.CartRepository;
import com.bikestore.repository.ColorRepository;
import com.bikestore.repository.OrderDetailRepository;
import com.bikestore.repository.OrderRepository;
import com.bikestore.repository.PaymentRepository;
import com.bikestore.repository.ProductDetailRepository;
import com.bikestore.repository.ProductRepository;
import com.bikestore.repository.SizeRepository;
import com.bikestore.repository.StatusOderRepository;
import com.bikestore.service.DataRespone;
import com.bikestore.service.JwtUtil;
//import com.bikestore.utils.ExpiringObjectService;
import com.bikestore.utils.JSONParse;
//import com.bikestore.utils.PasswordEncoder;

import jakarta.mail.Session;
import jakarta.persistence.criteria.Order;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import okhttp3.Response;

@RestController
public class CartAPI {
	@Autowired
	HttpServletRequest request;
	@Autowired
	private VNPayConfig vnpayConfig;
//	@Autowired
//	Tran
	@Autowired
	ProductDetailRepository productDetailRepository;
	@Autowired
	CartDetailRepository cartDetailRepository;
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	AddressRepository addressRepository;
	@Autowired
	StatusOderRepository statusOderRepository;
	@Autowired
	PaymentRepository paymentRepository;
	@Autowired
	OrderRepository oderRepository;
	@Autowired
    OrderDetailRepository orderDetailRepository;
	@Autowired
	HttpServletResponse respone;
	
	@GetMapping("/user/auth/cart/view")
	public DataRespone<Object> getCartItems() {
		Account account =(Account)request.getAttribute("user");
		DataRespone<Object> dataMap=new DataRespone<Object>();
		ArrayList<CartDetail>list= (ArrayList<CartDetail>) cartDetailRepository.findByAccount(account);
		return dataMap.addData("items",list);
	}
	
	
	@PostMapping("/user/auth/cart/preparecart")
	public DataRespone<Object> payCart(@RequestBody List<Integer> numbers) {
		Account a=(Account) request.getAttribute("user");
		List<CartDetail> cartDe=cartDetailRepository.findByAccountIdAndIds(a.getId(), numbers);
		if(numbers.size()<cartDe.size()+1) {
			return new DataRespone<Object>().addData("code",200).addData("cart",cartDe);
		}
		return new DataRespone<Object>().addData("code",400).addData("message","Thông tin cửa hàng thông hợp lệ !!!");
	}
	
	@GetMapping("/user/auth/cart/adress")
	public DataRespone<Object> getAdress(){
		DataRespone<Object> dataMap=new DataRespone<Object>();
		Account a=(Account) request.getAttribute("user");
		return dataMap.addData("adress", addressRepository.findBListyAccount(a.getId()));
	}
	
	@GetMapping("/user/auth/cart/countcart")
	public DataRespone<Object> getCountCart(){
		Account account =(Account)request.getAttribute("user");
		DataRespone<Object> dataMap=new DataRespone<Object>();
		return dataMap.addData("code",200).addData("countCart", cartDetailRepository.getTotalQuantityByAccountId((long)account.getId()));
	}
	
	
	@PostMapping("/user/auth/cart/order1")
	@Transactional
	public DataRespone<Object> oder(@RequestParam(name="shipeFee",defaultValue ="0")Integer shipfee,@RequestParam(name="idA",defaultValue = "1")Integer idAdress,@RequestParam(name="note",defaultValue = "")String note,@RequestParam(name = "idcs")ArrayList<Integer> ids,@RequestParam(name = "quantity")ArrayList<Integer> quantity) {
		DataRespone<Object> dataMap=new DataRespone<Object>();
		Account account = (Account) request.getAttribute("user");
		List<CartDetail> l=	cartDetailRepository.findByAccountIdAndIds(account.getId(),ids);
		if(ids.size()==l.size()) {
			Orders orders = new Orders();
			orders.setOrder_date(new Date());
			orders.setAccount(account);
			orders.setTotal(282828);
			orders.setNote(note);
			orders.setFee(shipfee);
			orders.setStatus(statusOderRepository.findById(1).get());
			orders.setAddress(addressRepository.findById(idAdress).get()); 
			orders.setPayment(paymentRepository.findById(1).get());
			oderRepository.saveAndFlush(orders);			
			IntStream.range(0, l.size()).forEach(i -> l.get(i).setQuantity(quantity.get(i))); 
	       ArrayList<OrderDetail>ol=new ArrayList<OrderDetail>();
	       l.forEach(v->{
	        	ol.add( new OrderDetail(orders,v.getProductDetail(),v.getQuantity()));
	       });
	       orderDetailRepository.saveAllAndFlush(ol);
	       cartDetailRepository.deleteAllByIdInBatch(ids);
	       return dataMap.addData("code",200).addData("message","Đã đặt hàng thành công !!!");
		}
		return dataMap.addData("code",400).addData("message","Có sự thay đổi trong giỏ hàng của bạn vui lòng kiểm tra lại!!!");
	}

	
	@PostMapping("/user/auth/cart/order2")
	public DataRespone<Object> order2(@RequestParam(name="shipeFee",defaultValue ="0")Integer shipfee,@RequestParam(name="idA",defaultValue = "1")Integer idAdress,@RequestParam(name="note",defaultValue = "")String note,@RequestParam(name = "idcs")ArrayList<Integer> ids,@RequestParam(name = "quantity")ArrayList<Integer> quantity){
		DataRespone<Object> dataMap=new DataRespone<Object>();
		Account account = (Account) request.getAttribute("user");
		Orders orders=new Orders();
		orders.setNote(note);
		
		orders.setOrder_date(new Date());
		orders.setStatus(statusOderRepository.findById(2).get());
		orders.setAddress(addressRepository.findById(idAdress).get()); 
		orders.setPayment(paymentRepository.findById(1).get());
		List<CartDetail> l=	cartDetailRepository.findByAccountIdAndIds(account.getId(),ids);
        ArrayList<OrderDetail>ol=new ArrayList<OrderDetail>();
        l.forEach(v->{
        	ol.add( new OrderDetail(v.getId(),v.getProductDetail(),v.getQuantity()));
        	orders.setTotal(orders.getTotal()+(v.getQuantity()*v.getProductDetail().getPrice()));
        });
        orders.setFee(shipfee);
        orders.orderDetails=ol;
        orders.uuid=UUID.randomUUID().toString();
        String url="http://localhost/user/pay/finish?uuid="+orders.uuid;
		try {
			return dataMap.addData("code",200).addData("url",redirectUrl(orders.getTotal(),orders.getFee(),url)).addData("itemsSelected",new JwtUtil().createTokendecodeTokenJSON(JSONParse.convertToJson(orders)));
		} catch (Exception e) {
		}
		return dataMap.addData("code",400).addData("message","Có lỗi xảy ra với giỏ hàng của bạn");
	}
	
	
	@PostMapping("/user/auth/cart/updatecart")
	public DataRespone<Object> updateCartItem(@RequestParam(name = "cartid",defaultValue = "-1")Integer cartId, @RequestParam(name = "quantity",defaultValue = "1")Integer quantity){
		DataRespone<Object> dataMap=new DataRespone<Object>();
		CartDetail cartDetail = cartDetailRepository.findById(cartId).get();
		Account account =(Account)request.getAttribute("user");
		if (cartDetail!=null&&cartDetail.getAccount().getId()==account.getId()) {
			ProductDetail productDetail = productDetailRepository.findById(cartDetail.getProductDetail().getId()).get();
			if(quantity<productDetail.getQuantity()+1&&quantity>0) {
				cartDetail.setQuantity(cartDetailRepository.updateQuantity(cartId,quantity)>0?quantity:cartDetail.getQuantity());
				return dataMap.addData("code",200).addData("message","Đã cập nhật giỏ hàng").addData("item",quantity);
			}
			return dataMap.addData("code",400).addData("message","Số lượng trong giỏ hàng không đủ bạn ơi").addData("item",cartDetail.getQuantity());
		}
		return dataMap.addData("code",403).addData("message","Đơn hàng không phải của bạn").addData("item",cartDetail.getQuantity());
	}	
	
	
	@PostMapping("/user/auth/cart/deletecart")
	@Transactional
	public DataRespone<Object> deletecart(@RequestParam(name = "cartid",defaultValue = "-1")Integer cartId){
		DataRespone<Object> dataMap=new DataRespone<Object>();
		CartDetail cartDetail = cartDetailRepository.findById(cartId).get();
		Account account =(Account)request.getAttribute("user");
	
		if (cartDetail!=null&&cartDetail.getAccount().getId()==account.getId()) {
			
			cartDetailRepository.deleteById(cartId);
			return dataMap.addData("code",200).addData("message","Đã xóa khỏi giỏ hàng");
		}
		return dataMap.addData("code",403).addData("message","Đơn hàng không phải của bạn");
	}	
	
	
	@PostMapping("/user/auth/cart/add")
	@Transactional
	public DataRespone<Object> postMethodName( @RequestParam("productId") Integer productId,@RequestParam("quantity") Integer quantity){
        Account account =(Account)request.getAttribute("user");
        ProductDetail productDetail = productDetailRepository.findByProductId(productId);
        DataRespone<Object> dataMap=new DataRespone<Object>();
        CartDetail cartItem = cartDetailRepository.findByAccountAndProductDetail(account, productDetail);
        if (cartItem!=null) {
            if ((productDetail.getQuantity()-(cartItem.getQuantity()+quantity) )>-1) {
            	if(cartDetailRepository.updateQuantityByAccountIdAndProductDetailId(quantity,account.getId(),productId)>0) {
            		return dataMap.addData("code",200).addData("message","Đã Thêm thành công vào giỏ hàng!!!").addData("sizeCart",1);
            	}
            	return dataMap.addData("code",500).addData("message","Thêm thất bại vui lòng thử lại sau !!!").addData("sizeCart",1);
			}
        }else {
        	if (( quantity) <productDetail.getQuantity()+1) {
                CartDetail newCartDetail = new CartDetail(account,productDetail,quantity);
                cartDetailRepository.saveAndFlush(newCartDetail);
                return dataMap.addData("code",200).addData("message","Thêm thành công vào giỏ hàng !!!");
			}
        }
        return dataMap.addData("code",400).addData("message","Vui lòng đặt sản phẩm  ít hơn !!!");
    }
	
	
	@RequestMapping("/user/pay/finish")
	public void g(@RequestParam(name = "uuid",defaultValue = "")String uuid){
		try {
			respone.sendRedirect("http://localhost:3000/lastpay/"+uuid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@PostMapping("/user/auth/lastpayment")
	@Transactional
	public DataRespone<Object> lastP(@RequestParam(name="uuid",defaultValue = "")String uuid,@RequestParam(name="items",defaultValue = "")String items) throws Exception{
		DataRespone<Object> dataMap=new DataRespone<Object>();
        Account account =(Account)request.getAttribute("user");
		if(!uuid.equals("")&&!items.equals("")) {
			String json=new JwtUtil().decodeTokenJSON(items);
			Orders o=JSONParse.convertFromJson(json,Orders.class);
			o.setPayment( paymentRepository.findById(2).get());
			o.setAccount(account);
			if(o.uuid.equals(uuid)){
				ArrayList<OrderDetail> l=o.orderDetails;
				oderRepository.saveAndFlush(o);
				ArrayList<Integer>k=new ArrayList<Integer>();
				l.forEach(v->{
					v.setOrder(o);
					k.add(v.getId());
				});
				orderDetailRepository.saveAllAndFlush(l);
				cartDetailRepository.deleteAllByIdInBatch(k);
				dataMap.addData("code",200).addData("message","Thanh toán đơn hàng thành công,Về trang chủ >>>>");
			}
		}
		return dataMap.addData("code",400).addData("message","Thanh toán Thất bại không hợp lệ");
	}
	
	
	private String vnp_HashSecret = "UE5AQGNZSQFJD9VOXA4HAIFMYH4FZ1J0";
	private String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
	public String redirectUrl(double amount,double shippingFee,String url) throws UnsupportedEncodingException{
		String vnp_Version = "2.1.0";
		String vnp_Command = "pay";
		String orderType = "billpayment";
		String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
		String vnp_IpAddr = "127.0.0.1";
		String vnp_TmnCode = "QIPASG5N";
		Map<String, String> vnp_Params = new HashMap<>();
		vnp_Params.put("vnp_Version", vnp_Version);
		vnp_Params.put("vnp_Command", vnp_Command);
		vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
		vnp_Params.put("vnp_Amount", String.valueOf((int) (amount*1000)));
		vnp_Params.put("vnp_CurrCode", "VND");
		vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
		vnp_Params.put("vnp_OrderInfo",UUID.randomUUID().toString());
		vnp_Params.put("vnp_OrderType", orderType);
		vnp_Params.put("vnp_Locale", "vn");
		vnp_Params.put("vnp_ReturnUrl",url);	
		vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String vnp_CreateDate = formatter.format(cld.getTime());
		vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
		cld.add(Calendar.MINUTE, 15);
		String vnp_ExpireDate = formatter.format(cld.getTime());
		vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
		List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
		Collections.sort(fieldNames);
		StringBuilder hashData = new StringBuilder();
		StringBuilder query = new StringBuilder();
		Iterator<String> itr = fieldNames.iterator();
		while (itr.hasNext()) {
			String fieldName = itr.next();
			String fieldValue = vnp_Params.get(fieldName);
			if (fieldValue != null && fieldValue.length() > 0) {
				hashData.append(fieldName);
				hashData.append('=');
				hashData.append(URLEncoder.encode(fieldValue, "UTF-8"));
				query.append(URLEncoder.encode(fieldName, "UTF-8"));
				query.append('=');
				query.append(URLEncoder.encode(fieldValue, "UTF-8"));
				if (itr.hasNext()) {
					query.append('&');
					hashData.append('&');
				}
			}
		}
		String queryUrl = query.toString();
		String vnp_SecureHash = HmacUtils.hmacSha512Hex(vnp_HashSecret, hashData.toString());
		queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
		String paymentUrl = vnpayConfig.getVnp_Url() + "?" + queryUrl;
		return paymentUrl;
	}
	
	
}
