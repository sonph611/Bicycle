package com.bikestore.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bikestore.model.CartDetail;
import com.bikestore.model.Category;
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
import com.bikestore.service.LocationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class viewController {
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
	OrderRepository orRepository;

	Integer idCategory = 0;
	List<Integer> colorFilter = new ArrayList<>();
	List<Integer> sizeFilter =  new ArrayList<>();
	List<Integer> colorFilterC = new ArrayList<>();
	List<Integer> sizeFilterC =  new ArrayList<>();
	double priceMinvl = -1;
	double priceMaxvl = -1;

	@GetMapping("/home")
	public String ShowHome(Model model, 
	                       @RequestParam("pageNo") Optional<Integer> pageNo, 
	                       @RequestParam("pageSize") Optional<Integer> pageSize,
	                       @RequestParam(name = "keyword", defaultValue = "") String keyWord,
	                       //Dữ liệu login FB
	                       OAuth2AuthenticationToken oAuth2AuthenticationToken
			) {
	    int currentPage = pageNo.orElse(0);
	    int pageSizeValue = pageSize.orElse(8);
	    Integer idAccount = (Integer) httpSession.getAttribute("user"); 
	    List<CartDetail> sizeCart = new ArrayList<>();
	    sizeCart = cartRepository.findProduct(idAccount);
	    httpSession.setAttribute("quantityProduct", sizeCart.size());
	    Pageable pageable =PageRequest.of(currentPage, pageSizeValue, Sort.by("name").ascending());
	    Page<Object[]> productPage = productRepository.findTop8SaleSQL(keyWord,pageable);
	    
	    Page<Object[]> Top8new = productRepository.findTop8NewSQL(keyWord,pageable);
	    
	    List<Category> categoriesHeader = categoryRepository.findAll();
	    httpSession.setAttribute("categoriesHeader", categoriesHeader);
	    model.addAttribute("listnew", Top8new);
	    model.addAttribute("listItem", productPage);
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("totalPages", productPage.getTotalPages());
	    model.addAttribute("pageNumbers", getPageNumbers(productPage));

	 
	    request.setAttribute("view", "home.html");
	    return "user/fontend/home";
	}
	private List<Integer> getPageNumbers(Page<Object[]> productPage) {
	    int totalPages = productPage.getTotalPages();
	    List<Integer> pageNumbers = new ArrayList<>();
	    for (int i = 0; i < totalPages; i++) {
	        pageNumbers.add(i);
	    }
	    return pageNumbers;
	}

	@GetMapping("/account/login")
	public String  loginViews() {
		
		return "account/login";
	}
}
