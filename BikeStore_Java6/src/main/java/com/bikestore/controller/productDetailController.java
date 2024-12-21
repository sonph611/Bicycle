package com.bikestore.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.bikestore.model.Color;
import com.bikestore.model.Product;
import com.bikestore.model.ProductDetail;
import com.bikestore.model.Size;
import com.bikestore.repository.CategoryRepository;
import com.bikestore.repository.ColorRepository;
import com.bikestore.repository.ProductDetailRepository;
import com.bikestore.repository.ProductRepository;
import com.bikestore.repository.SizeRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class productDetailController {
	@Autowired
	HttpServletRequest request;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	ColorRepository colorRepository;
	@Autowired
	SizeRepository sizeRepository;
	@Autowired
	ProductDetailRepository productDetailRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ServletContext servletContext;
	@Autowired
	Cloudinary cloudinary;

	String error = "";
	String success = "";

	@GetMapping("/admin/product/detail/{id}")
	public String viewProductDetail(Model model, @PathVariable("id") Integer id,
			@RequestParam("pageNo") Optional<Integer> pageNo, @RequestParam("pageSize") Optional<Integer> pageSize,
			@RequestParam("activeTab") Optional<String> activeTab,
			@RequestParam(value = "resetError", required = false) boolean resetError) {
		
		if (resetError) {
			error = "";
			success = "";
		}

		if (!error.isEmpty()) {
			model.addAttribute("error", error);
		} else {
			model.addAttribute("error", "");
		}

		if (!success.isEmpty()) {
			model.addAttribute("success", success);
		} else {
			model.addAttribute("success", "");
		}

		int currentPage = pageNo.orElse(0);
		int pageSizeValue = pageSize.orElse(5);
		String activeTabValue = activeTab.orElse("product-edition");
		Pageable pageable = PageRequest.of(currentPage, pageSizeValue, Sort.by("id").ascending());

		Page<ProductDetail> productDetailList = productDetailRepository.findAllDetailByid(pageable, id);
		Product product = productRepository.findById(id).get();
		List<Color> colors = colorRepository.findAllByStatus();
		List<Size> sizes = sizeRepository.findAllByStatus();
		ProductDetail productDetail = new ProductDetail();
		
		
		model.addAttribute("product", product);
		model.addAttribute("productDetailList", productDetailList.getContent());
		model.addAttribute("colors", colors);
		model.addAttribute("sizes", sizes);
		model.addAttribute("productDetail", productDetail);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", productDetailList.getTotalPages());
		model.addAttribute("pageNumbers", getPageNumbers(productDetailList));
		model.addAttribute("activeTab", activeTabValue);

		request.setAttribute("view", "bikeProductDetail.html");
		return "admin/fontend/bikeProductDetail";
	}

	@PostMapping("/admin/product/detail/insert")
	public String adminProductDetailInsert(@Valid @ModelAttribute("productDetail") ProductDetail productDetail,
	                                       BindingResult bindingResult, @RequestPart("image") MultipartFile photo,
	                                       @RequestParam("product_id") Integer id, @RequestParam("price") String price,
	                                       @RequestParam("quantity") String quantity) {

	    boolean check = false;
	    double priceValue = 0;
	    double quantityValue = 0;

	    try {
	        priceValue = Double.parseDouble(price);
	        if (priceValue < 1000) {
	            error = "Giá không được bé hơn 1000!";
	            check = true;
	        }
	    } catch (NumberFormatException e) {
	        error = "Giá phải là một số hợp lệ!";
	        check = true;
	    }

	    try {
	        quantityValue = Double.parseDouble(quantity);
	        if (quantityValue < 0) {
	            error = "Số lượng không được bé hơn 0!";
	            check = true;
	        }
	    } catch (NumberFormatException e) {
	        error = "Số lượng phải là một số hợp lệ!";
	        check = true;
	    }

	    List<ProductDetail> productDetails = productDetailRepository.findDetailByColorAndSize(id,
	            productDetail.getColor().getId(), productDetail.getSize().getId());
	    if (!productDetails.isEmpty()) {
	        error = "Biến thể đã tồn tại!";
	        check = true;
	    }

	    if (check) {
	        return "redirect:/admin/product/detail/" + id;
	    } else {
	        Product product = productRepository.findById(id).orElse(null);
	        productDetail.setProduct(product);

	        if (!photo.isEmpty()) {
	            try {
	                // Upload lên Cloudinary
	                Map uploadResult = cloudinary.uploader().upload(photo.getBytes(), ObjectUtils.emptyMap());
	                String photoUrl = uploadResult.get("secure_url").toString();
	                productDetail.setImagedetail(photoUrl);
	            } catch (IOException e) {
	                e.printStackTrace();
	                error = "Đã xảy ra lỗi khi tải lên ảnh!";
	                return "redirect:/admin/product/detail/" + id;
	            }
	        }

	        productDetailRepository.saveAndFlush(productDetail);

	        if (product != null) {
	            List<Object> listqty = productRepository.findCountByProductSQL(id);
	            int qty = (int) listqty.get(0);
	            product.setQuantity(qty);
	            productRepository.saveAndFlush(product);
	        }

	        error = "";
	        success = "Sản phẩm biến thể đã được thêm thành công!";
	        return "redirect:/admin/product/detail/" + id;
	    }
	}


	
	@GetMapping("/admin/product/detail/edit/{id}")
	public String editProductDetail(Model model, @PathVariable("id") Integer id,
			@RequestParam("pageNo") Optional<Integer> pageNo, @RequestParam("pageSize") Optional<Integer> pageSize,
			@RequestParam("activeTab") Optional<String> activeTab,
			@RequestParam(value = "resetError", required = false) boolean resetError) {
		
		if (resetError) {
			error = "";
			success = "";
		}

		if (!error.isEmpty()) {
			model.addAttribute("error", error);
		} else {
			model.addAttribute("error", "");
		}

		if (!success.isEmpty()) {
			model.addAttribute("success", success);
		} else {
			model.addAttribute("success", "");
		}

		int currentPage = pageNo.orElse(0);
		int pageSizeValue = pageSize.orElse(5);
		String activeTabValue = activeTab.orElse("product-edition");
		Pageable pageable = PageRequest.of(currentPage, pageSizeValue, Sort.by("id").ascending());

		ProductDetail productDetail = productDetailRepository.findById(id).orElse(null);

		List<Color> colors = colorRepository.findAll();
		List<Size> sizes = sizeRepository.findAll();
		Product product = productRepository.findById(productDetail.getProduct().getId()).get();
		
		
		
		Page<ProductDetail> productDetailList = productDetailRepository.findAllDetailByid(pageable,
				productDetail.getProduct().getId());

		model.addAttribute("product", product);
		model.addAttribute("productDetailList", productDetailList.getContent());
		model.addAttribute("productDetail", productDetail);
		model.addAttribute("colors", colors);
		model.addAttribute("sizes", sizes);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", productDetailList.getTotalPages());
		model.addAttribute("pageNumbers", getPageNumbers(productDetailList));
		model.addAttribute("activeTab", activeTabValue);
		request.setAttribute("view", "bikeProductDetail.html");
		return "admin/fontend/bikeProductDetail";

	}

	private List<Integer> getPageNumbers(Page<ProductDetail> productPage) {
		int totalPages = productPage.getTotalPages();
		List<Integer> pageNumbers = new ArrayList<>();
		for (int i = 0; i < totalPages; i++) {
			pageNumbers.add(i);
		}
		return pageNumbers;
	}

	@PostMapping("/admin/product/detail/update")
	public String updateProductDetail(@Valid Model model, @ModelAttribute("productDetail") ProductDetail productDetail,
			BindingResult bindingResult, @RequestPart("image") MultipartFile photo, @RequestParam("id") Integer id,
			@RequestParam("price") String price, @RequestParam("quantity") String quantity,@RequestParam("product_id") Integer productId) {

		ProductDetail existingProductDetail = productDetailRepository.findById(id).orElse(null);
		
		if (existingProductDetail != null) {

			boolean check = false;
			double priceValue = 0;
			double quantittValue = 0;
			try {
				priceValue = Double.parseDouble(price);
				if (priceValue < 1000) {
					error = "Giá không được bé hơn 1000! ";
					check = true;
				}
			} catch (NumberFormatException e) {
				error = "Giá phải là một số hợp lệ! ";
				check = true;
			}
			try {
				quantittValue = Double.parseDouble(quantity);
				if (quantittValue < 0) {
					error = "Số lượng không được bé hơn 0! ";
					check = true;
				}
			} catch (NumberFormatException e) {
				error = "Số lượng phải là một số hợp lệ! ";
				check = true;
			}

			List<ProductDetail> productDetails = productDetailRepository.findDetailByColorAndSize(productId,
					productDetail.getColor().getId(), productDetail.getSize().getId(),productDetail.getId());
			if (!productDetails.isEmpty()) {
				error = "Biến thể đã tồn tại ! ";
				check = true;

			}
			if (check == true) {
				success="";
				return "redirect:/admin/product/detail/edit/" + id;
			} else {

				existingProductDetail.setColor(productDetail.getColor());
				existingProductDetail.setSize(productDetail.getSize());
				existingProductDetail.setPrice(productDetail.getPrice());
				existingProductDetail.setQuantity(productDetail.getQuantity());

				if (!photo.isEmpty()) {
					String fileName = photo.getOriginalFilename();
					String realPath = servletContext.getRealPath("/images/" + fileName);
					Path path = Path.of(realPath);
					if (!Files.exists(path)) {
						try {
							Files.createDirectories(path);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					File file = new File(realPath);
					try {
						photo.transferTo(file);
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					existingProductDetail.setImagedetail(fileName);
				} else {
					productDetail.setImagedetail(existingProductDetail.getImagedetail());
				}

				productDetailRepository.saveAndFlush(existingProductDetail);
				Product product = productRepository.findById(productId).get();
				if (product != null) {
					List<Object> listqty = productRepository.findCountByProductSQL(id);
					 if (!listqty.isEmpty()) {
		                    int qty = (int) listqty.get(0);
		                    product.setQuantity(qty);
		                    productRepository.saveAndFlush(product);
		                }
				}
				
				error = "";
				success = "Sản phẩm biến thể đã được cập nhật thành công!";
				return "redirect:/admin/product/detail/edit/" + id;
			}
		} else {
			// Handle the case where the product detail with the specified ID is not found
			error = "Cập nhật không thành công!";
			return "redirect:/admin/product/detail/edit/" + id;
		}
	}

}
