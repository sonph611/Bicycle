package com.bikestore.controller.API;

import org.springframework.web.bind.annotation.RestController;

import com.bikestore.model.Account;
import com.bikestore.model.Category;
import com.bikestore.model.Color;
import com.bikestore.model.Product;
import com.bikestore.model.ProductDetail;
import com.bikestore.model.Size;
import com.bikestore.repository.CartDetailRepository;
import com.bikestore.repository.CartRepository;
import com.bikestore.repository.CategoryRepository;
import com.bikestore.repository.ColorRepository;
import com.bikestore.repository.FilterRepository;
import com.bikestore.repository.OrderDetailRepository;
import com.bikestore.repository.OrderRepository;
import com.bikestore.repository.ProductDetailRepository;
import com.bikestore.repository.ProductRepository;
import com.bikestore.repository.ProfileRepository;
import com.bikestore.repository.SizeRepository;
import com.bikestore.service.DataRespone;
import com.bikestore.service.LocationService;

import io.jsonwebtoken.lang.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.Console;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class ProductSAPI {
	
	@Autowired
	HttpServletRequest request;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ColorRepository colorRepository;
	@Autowired
	SizeRepository sizeRepository;
	@Autowired
	FilterRepository filterRepository;
	@Autowired
	ProductDetailRepository productDetailRepository;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	OrderDetailRepository detailRepository;

	@Autowired
	HttpSession session;

	@Autowired
	private LocationService locationService;
	@Autowired
	HttpSession httpSession;
	@Autowired
	ProfileRepository profileRepository;
	@Autowired
	CartRepository cartRepository;
	@Autowired
	OrderDetailRepository orderDetailRepository;
	@Autowired
	ProductDetailRepository variant;
	@Autowired
	CartDetailRepository cartDetailRepository;
	@Autowired
	OrderRepository orRepository;
	Integer idCategory = 0;
	List<Integer> colorFilter = new ArrayList<>();
	List<Integer> sizeFilter =  new ArrayList<>();
	List<Integer> colorFilterC = new ArrayList<>();
	List<Integer> sizeFilterC =  new ArrayList<>();
	double priceMinvl = -1;
	double priceMaxvl = -1;


	@GetMapping("/user/productssearch")
	public DataRespone<Object>  productsViews(Model model, @RequestParam("pageNo") Optional<Integer> pageNo, 
			 @RequestParam("pageSize") Optional<Integer> pageSize,
			 @RequestParam(name =  "idcty", defaultValue = "7") Integer id,
			 @RequestParam(name = "keyword", defaultValue = "") String keyWord,
			 @RequestParam(name =  "minPrice", defaultValue = "0") double minPrice,
			 @RequestParam(name =  "maxPrice", defaultValue = "12900000") double maxPrice,
			 @RequestParam(name =  "idcl", defaultValue = "") String idColor,
			 @RequestParam(name =  "ids", defaultValue = "") String idSize) {
		List<Integer> idColors=idColor.trim().equals("")?colorRepository.findIdsByIds():dpa(idColor);
		List<Integer> idSizes=idSize.trim().equals("")?sizeRepository.findAllIds():dpa(idSize);
		int currentPage = pageNo.orElse(0);
		int pageSizeValue = pageSize.orElse(5);
		Pageable pageable =PageRequest.of(currentPage, pageSizeValue, Sort.by("name").ascending());	
		Page<Object> productPage = filterRepository.findByCategory(id, keyWord, minPrice, maxPrice,idColors,idSizes, pageable);
		List<Color> colors = colorRepository.findAll();
		List<Size> sizes = sizeRepository.findAll();
		DataRespone<Object> data=new DataRespone<Object>();
		data.addData("colors", colors).addData("sizes", sizes).addData("products",productPage);    
		return data;
	}
	
	public List<Integer> dpa(String ids) {
		String[] ls=ids.split(" ");
		List<Integer> l=new ArrayList<Integer>();
		for (String c: ls) {
			String b=c;
			l.add(Integer.parseInt(c));
		}
		return l;
	}
	
	
	@GetMapping("/user/productssearch1")
	public DataRespone<Object>  productsViews1( 
			 @RequestParam(name =  "idcty", defaultValue = "7") Integer id) {
		int currentPage = 0;
		int pageSizeValue = 5;
		Pageable pageable =PageRequest.of(currentPage, pageSizeValue, Sort.by("name").ascending());	
		Page<Object> productPage = filterRepository.findByCategory1(id, pageable);
		List<Color> colors = colorRepository.findAll();
		List<Size> sizes = sizeRepository.findAll();
		DataRespone<Object> data=new DataRespone<Object>();
		data.addData("colors", colors).addData("sizes", sizes).addData("products",productPage);    
		return data;
	}
	
	//findByCategory1
	
	@GetMapping("/user/productdetail/{id}")
	public DataRespone<Object> DetailViews( @PathVariable("id") Integer id) {
		Product product = productRepository.findById(id).orElse(null);
		List<Object[]> colors = colorRepository.findColorByProductSQL(id);
		DataRespone<Object> dataMap=new DataRespone<Object>();
		return dataMap.addData("product", product).addData("code",200).addData("colorProduct",colors);
	}
	
	public Integer getCountCarts() {
		Account account =(Account)request.getAttribute("user");
		return cartDetailRepository.getTotalQuantityByAccountId((long)account.getId());
	}
	
	
	@GetMapping("/user/variant")
	public DataRespone<Object> getVariantByProductIdAndColorId(@RequestParam(name="idpd",defaultValue ="1")Integer idpd,
			@RequestParam(name="idcl",defaultValue ="1")Integer idcl){
		DataRespone<Object> dataMap=new DataRespone<Object>();
		return dataMap.addData("data", variant.findByColorIdAndProductId(idcl,idpd));
	}
	
	@GetMapping("user/products")
	public DataRespone ShowHome(Model model, 
	                       @RequestParam("pageNo") Optional<Integer> pageNo, 
	                       @RequestParam("pageSize") Optional<Integer> pageSize,
	                       @RequestParam(name = "keyword", defaultValue = "") String keyWord) {
	    int currentPage = pageNo.orElse(0);
	    int pageSizeValue = pageSize.orElse(8);
	    Pageable pageable =PageRequest.of(currentPage, pageSizeValue, Sort.by("name").ascending());
	    DataRespone<Object> dataMap=new DataRespone();
	    dataMap.addData("listNew",(ArrayList) productRepository.findTop8NewSQL1(keyWord, pageable)).
	    addData("listSale", productRepository.findTop8SaleSQL(keyWord, pageable).getContent());
	    return dataMap;
	}
	
	
	@GetMapping("/user/productheader")
	public DataRespone<Object> getProductHeader()
	{		System.out.println("hebuduwfwffwwww");
		DataRespone<Object> dataMap=new DataRespone();
	    dataMap.addData("productHeader", categoryRepository.findAll());
	    return dataMap;
	}

	
}
