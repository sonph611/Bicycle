package com.bikestore.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bikestore.model.Orders;
import com.bikestore.model.Product;
import com.bikestore.model.ProductDetail;
import com.bikestore.model.Status;
import com.bikestore.repository.OrderDetailRepository;
import com.bikestore.repository.OrderRepository;
import com.bikestore.repository.ProductDetailRepository;
import com.bikestore.repository.ProductRepository;
import com.bikestore.repository.StatusOderRepository;
import com.bikestore.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class orderController {
	@Autowired
	HttpServletRequest request;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	OrderDetailRepository orderDetailRepository;
	@Autowired
	StatusOderRepository statusOderRepository;
	@Autowired
	HttpSession session;
	@Autowired
	ProductDetailRepository detailRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	ProductDetailRepository productDetailRepository;
	@Autowired
	private EmailService emailService;

	@GetMapping("/home/profile2")
	public String ShowProfile(Model model) {
		
		List<Object[]> OrderDetailPage = orderDetailRepository.findAllSQL((Integer) session.getAttribute("user"));

		List<Object[]> OrderDetailPage2 = orderRepository.findAllSQL2((Integer) session.getAttribute("user"));

		// Debug để kiểm tra liệu có dữ liệu được truy vấn hay không
		System.out.println("Size of OrderDetailPage: " + OrderDetailPage.size());
		System.out.println("Size of OrderDetailPage2: " + OrderDetailPage2.size());

		// Thêm các thông tin debug để xác nhận rằng dữ liệu có trong list hay không
		if (OrderDetailPage.isEmpty()) {
			System.out.println("list 1 rỗng");
		} else {
			System.out.println("list 1 có dữ liệu");
			for (Object[] item : OrderDetailPage) {
				System.out.println(Arrays.toString(item));
			}
		}
		if (OrderDetailPage2.isEmpty()) {
			System.out.println("list 2 rỗng");
		} else {
			System.out.println("list 2 có dữ liệu");
			for (Object[] item : OrderDetailPage2) {
				System.out.println(Arrays.toString(item));
			}
		}

		// Add data to model attributes
		model.addAttribute("listItem", OrderDetailPage);
		model.addAttribute("listItem2", OrderDetailPage2);

		request.setAttribute("view", "Profile2.jsp");
		return "user/font-end/index";
	}

	@GetMapping("/home/profile/order/{id}")
	public String ShowOrderDetail(Model model, @PathVariable("id") Integer id,
			@RequestParam(name="nameStatusfind",defaultValue = "") String nameStatusfind) {

		List<Object[]> listProduct = orderRepository.findAllProductByOrderIdSQL(id);
		List<Object[]> listOrderDetai = orderRepository.findOrderDetailSQL(id);

		Orders orders = orderRepository.findById(id).get();
		orders.setId(id);

		Status status2;
		if (nameStatusfind.equalsIgnoreCase("ht")) {
			status2 = statusOderRepository.findById(6).get();
		} else {
			status2 = orders.getStatus();
		}
		orders.setStatus(status2);
		orderRepository.saveAndFlush(orders);
		model.addAttribute("listOrderDetai", listOrderDetai.get(0));
		model.addAttribute("listProduct", listProduct);

		request.setAttribute("view", "OrderDetail.jsp");
		return "user/font-end/index";
	}

	@PostMapping("/home/profile/order/cancel")
	public String OrderCancelUser(Model model, @RequestParam("orderId") Integer id,
			@RequestParam("reason") String reason) {
		Orders orders = orderRepository.findById(id).orElse(null);
		if (orders == null) {
			// Handle case where order is not found
			return "errorPage";
		}

		orders.setId(id);

		Status status2 = statusOderRepository.findById(3).orElse(null);
		if (status2 == null) {
			// Handle case where status is not found
			return "errorPage";
		}

		orders.setStatus(status2);
		orders.setReason(reason);
		orderRepository.saveAndFlush(orders);

		List<Object[]> listProduct = orderRepository.findAllProductByOrderIdSQL(id);
		List<Object[]> listOrderDetai = orderRepository.findOrderDetailSQL(id);

		for (Object[] objects : listProduct) {
			Integer idd = (Integer) objects[6];
			Integer idpd = (Integer) objects[0];
			ProductDetail productDetail = detailRepository.findById(idd).orElse(null);
			if (productDetail == null) {
				continue;
			}
			int qty = (int) objects[5] + productDetail.getQuantity();
			System.out.println("Số lượng là: " + qty);
			productDetail.setId(productDetail.getId());
			productDetail.setQuantity(qty);
			productDetailRepository.saveAndFlush(productDetail);
			Product product = productRepository.findById(idpd).orElse(null);
			if (product != null) {
				List<Object> listqty = productRepository.findCountByProductSQL(id);
				if (!listqty.isEmpty()) {
					int qty1 = (int) listqty.get(0);
					product.setQuantity(qty1);
					productRepository.saveAndFlush(product);
				}
			}
		}

		if (!listOrderDetai.isEmpty()) {
			model.addAttribute("listOrderDetai", listOrderDetai.get(0));
		}
		model.addAttribute("listProduct", listProduct);
		request.setAttribute("view", "OrderDetail.jsp");
		return "user/font-end/index";
	}

	// -------------------------------------Admin---------------------------------

	@GetMapping("/admin/order")
	public String adminOrderView(Model model, 
	                             @RequestParam("activeTab") Optional<String> activeTab,
	                             @RequestParam("pageNo") Optional<Integer> pageNo, 
	                             @RequestParam("pageSize") Optional<Integer> pageSize,
	                             @RequestParam(name = "key", defaultValue = "") String keyWord,
	                             @RequestParam(name = "nameStatus", defaultValue = "Chờ xác nhận") String nameStatus) {

	    int currentPage = pageNo.orElse(0);
	    int pageSizeValue = pageSize.orElse(5);
	    String role = session.getAttribute("role").toString();
	    Pageable pageable = PageRequest.of(currentPage, pageSizeValue, Sort.by("id").ascending());
	    Page<Object[]> listOrder;
	    
	    if (keyWord.isEmpty()) {
	        listOrder = orderRepository.findAllOrderByStatusSQL(pageable, nameStatus);
	    } else {
	        listOrder = orderRepository.findAllOrderByStatusSQL2(pageable, keyWord);
	    }
	    
	    List<Status> listStatus = statusOderRepository.findAll();
	    String activeTabValue = activeTab.orElse("product-edition");
	    
	    model.addAttribute("listOrder", listOrder.getContent());
	    model.addAttribute("listStatus", listStatus);
	    model.addAttribute("activeTab", activeTabValue);
	    model.addAttribute("nameStatus", nameStatus);
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("totalPages", listOrder.getTotalPages());
	    model.addAttribute("pageNumbers", getPageNumbers(listOrder));
	    model.addAttribute("role", role);
	    request.setAttribute("view", "order.html");
	    
	    return "admin/fontend/order";
	}


	private List<Integer> getPageNumbers(Page<Object[]> productPage) {
		int totalPages = productPage.getTotalPages();
		List<Integer> pageNumbers = new ArrayList<>();
		for (int i = 0; i < totalPages; i++) {
			pageNumbers.add(i);
		}
		return pageNumbers;
	}

	@GetMapping("/admin/order/edit/{id}")
	public String OrderDetailView(Model model, @PathVariable("id") Integer id,
			@RequestParam("activeTab") Optional<String> activeTab, @RequestParam("pageNo") Optional<Integer> pageNo,
			@RequestParam("pageSize") Optional<Integer> pageSize,
			@RequestParam(name = "key", defaultValue = "") String keyWord,
			@RequestParam(name = "nameStatus", defaultValue = "Chờ xác nhận") String nameStatus) {
		
		int currentPage = pageNo.orElse(0);
		int pageSizeValue = pageSize.orElse(5);
		String role = session.getAttribute("role").toString();
		Pageable pageable = PageRequest.of(currentPage, pageSizeValue, Sort.by("id").ascending());

		List<Object[]> listProduct = orderRepository.findAllProductByOrderIdSQL(id);

		Page<Object[]> listOrder;
		if (keyWord.isEmpty()) {
			listOrder = orderRepository.findAllOrderByStatusSQL(pageable, nameStatus);
		} else {
			listOrder = orderRepository.findAllOrderByStatusSQL2(pageable, keyWord);
		}

		List<Object[]> listOrderDetai = orderRepository.findOrderDetailSQL(id);

		List<Status> listStatus = statusOderRepository.findAll();

		String activeTabValue = activeTab.orElse("product-edition");
		if (listOrderDetai.isEmpty()) {
			System.out.println("list 2 rỗng");
		} else {
			System.out.println("list 2 có dữ liệu");
			for (Object[] item : listOrderDetai) {
				System.out.println(Arrays.toString(item));
			}
		}

		if (!listOrderDetai.isEmpty()) {
		    model.addAttribute("listOrderDetai", listOrderDetai.get(0));
		} else {
		    model.addAttribute("listOrderDetai", null); 
		}

		model.addAttribute("listOrder", listOrder.getContent());
		model.addAttribute("listStatus", listStatus);
		model.addAttribute("listProduct", listProduct);
		model.addAttribute("activeTab", activeTabValue);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", listOrder.getTotalPages());
		model.addAttribute("pageNumbers", getPageNumbers(listOrder));
		model.addAttribute("role", role);
		
		request.setAttribute("view", "order.html");
		return "admin/fontend/order";
	}

	@GetMapping("/admin/order/success/{id}")
	public String OrderSuccess(Model model, @PathVariable("id") Integer id,
			@RequestParam("nameStatusfind") String nameStatusfind,
			@RequestParam("activeTab") Optional<String> activeTab, @RequestParam("pageNo") Optional<Integer> pageNo,
			@RequestParam("pageSize") Optional<Integer> pageSize,
			@RequestParam(name = "key", defaultValue = "") String keyWord,
			@RequestParam(name = "nameStatus", defaultValue = "Chờ xác nhận") String nameStatus) {

		int currentPage = pageNo.orElse(0);
		int pageSizeValue = pageSize.orElse(5);

		Pageable pageable = PageRequest.of(currentPage, pageSizeValue, Sort.by("id").ascending());

		Orders orders = orderRepository.findById(id).get();
		orders.setId(id);
		Status status2;
		if (nameStatusfind.equalsIgnoreCase("dxl")) {
			status2 = statusOderRepository.findById(2).get();
		} else if (nameStatusfind.equalsIgnoreCase("dvc")) {
			status2 = statusOderRepository.findById(4).get();
		} else if (nameStatusfind.equalsIgnoreCase("dgh")) {
			status2 = statusOderRepository.findById(5).get();
		} else if (nameStatusfind.equalsIgnoreCase("ht")) {
			status2 = statusOderRepository.findById(6).get();
		} else {
			status2 = statusOderRepository.findById(1).get();
		}

		orders.setStatus(status2);
		String activeTabValue = activeTab.orElse("product-edition");

		orderRepository.saveAndFlush(orders);

		Page<Object[]> listOrder;
		if (keyWord.isEmpty()) {
			listOrder = orderRepository.findAllOrderByStatusSQL(pageable, nameStatus);
		} else {
			listOrder = orderRepository.findAllOrderByStatusSQL2(pageable, keyWord);
		}

		List<Status> listStatus = statusOderRepository.findAll();

		model.addAttribute("listOrder", listOrder.getContent());
		model.addAttribute("activeTab", activeTabValue);
		model.addAttribute("listStatus", listStatus);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", listOrder.getTotalPages());
		model.addAttribute("pageNumbers", getPageNumbers(listOrder));
		request.setAttribute("view", "order.html");
		return "admin/fontend/order";
	}

	@PostMapping("/admin/order/cancel")
	public String OrderCancel(Model model, @RequestParam("orderId") Integer id, @RequestParam("reason") String reason,
			@RequestParam("activeTab") Optional<String> activeTab, @RequestParam("pageNo") Optional<Integer> pageNo,
			@RequestParam("pageSize") Optional<Integer> pageSize,
			@RequestParam(name = "key", defaultValue = "") String keyWord,
			@RequestParam(name = "nameStatus", defaultValue = "đang xử lý") String nameStatus) {

		int currentPage = pageNo.orElse(0);
		int pageSizeValue = pageSize.orElse(5);

		Pageable pageable = PageRequest.of(currentPage, pageSizeValue, Sort.by("id").ascending());

		List<Status> listStatus = statusOderRepository.findAll();

		Orders orders = orderRepository.findById(id).get();
		orders.setId(id);

		Status status2 = statusOderRepository.findById(3).get();
		orders.setStatus(status2);
		orders.setReason(reason);
		String activeTabValue = activeTab.orElse("product-edition");
		orderRepository.saveAndFlush(orders);

		List<Object[]> listProduct = orderRepository.findAllProductByOrderIdSQL(id);
		for (Object[] objects : listProduct) {
			Integer idd = (Integer) objects[6];
			Integer idpd = (Integer) objects[0];
			ProductDetail productDetail = detailRepository.findById(idd).orElse(null);
			if (productDetail == null) {
				continue;
			}
			int qty = (int) objects[5] + productDetail.getQuantity();
			System.out.println("Số lượng là: " + qty);
			productDetail.setId(productDetail.getId());
			productDetail.setQuantity(qty);
			productDetailRepository.saveAndFlush(productDetail);
			Product product = productRepository.findById(idpd).orElse(null);
			if (product != null) {
				List<Object> listqty = productRepository.findCountByProductSQL(id);
				if (!listqty.isEmpty()) {
					int qty1 = (int) listqty.get(0);
					product.setQuantity(qty1);
					productRepository.saveAndFlush(product);
				}
			}
		}

		Page<Object[]> listOrder;
		if (keyWord.isEmpty()) {
			listOrder = orderRepository.findAllOrderByStatusSQL(pageable, nameStatus);
		} else {
			listOrder = orderRepository.findAllOrderByStatusSQL2(pageable, keyWord);
		}

		model.addAttribute("listStatus", listStatus);
		model.addAttribute("listOrder", listOrder.getContent());
		model.addAttribute("activeTab", activeTabValue);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", listOrder.getTotalPages());
		model.addAttribute("pageNumbers", getPageNumbers(listOrder));
		request.setAttribute("view", "order.html");

		String emailTo = orders.getAccount().getEmail(); // Giả sử bạn có trường email trong Orders hoặc Account
		String subject = "Order Cancellation Notification";
		String text = "Xin chào <b>" + orders.getAccount().getFullname() + "</b>,<br/><br/>" + "Đơn hàng có mã <b>" + id
				+ "</b> đã bị hủy với lý do: <b>" + reason + "</b>.<br/><br/>"
				+ "Chân thành xin lỗi với sự bất tiện này.<br/><br/>"
				+ "Nếu bạn gặp sự cố, vui lòng liên hệ với bộ phận hỗ trợ thông qua email: "
				+ "<a href='mailto:hoangnam.novai6@gmail.com'>hoangnam.novai6@gmail.com</a>.<br/><br/>"
				+ "Trân trọng,<br/>" + "Đội ngũ hỗ trợ khách hàng.";

		try {
			emailService.sendEmail(emailTo, subject, text);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "admin/fontend/order";
	}

}
