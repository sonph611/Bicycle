package com.bikestore.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bikestore.model.Category;
import com.bikestore.model.Color;
import com.bikestore.model.Size;
import com.bikestore.repository.CategoryRepository;
import com.bikestore.repository.ColorRepository;
import com.bikestore.repository.SizeRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Controller
public class BicycleVariationsController {

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    HttpServletRequest request;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @GetMapping("/admin/bicycle-variations")
    public String showBicycleVariations(Model model,
                                        @RequestParam(defaultValue = "0") int pageColor,
                                        @RequestParam(defaultValue = "0") int pageSize,
                                        @RequestParam(defaultValue = "0") int pageCategory) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        int sizePerPage = 3;

        Pageable colorPageable = PageRequest.of(pageColor, sizePerPage, sort);
        Pageable sizePageable = PageRequest.of(pageSize, sizePerPage, sort);
        Pageable categoryPageable = PageRequest.of(pageCategory, sizePerPage, sort);

        Page<Size> sizes = sizeRepository.findAll(sizePageable);
        Page<Color> colors = colorRepository.findAll(colorPageable);
        Page<Category> categories = categoryRepository.findAll(categoryPageable);

        model.addAttribute("sizes", sizes);
        model.addAttribute("colors", colors);
        model.addAttribute("categories", categories);
        model.addAttribute("currentPageColor", pageColor);
        model.addAttribute("currentPageSize", pageSize);
        model.addAttribute("currentPageCategory", pageCategory);

        
        request.setAttribute("view", "BicycleVariations.html");

        
        return "admin/fontend/BicycleVariations";
    }



    @PostMapping("/admin/bicycle-variations/create")
    public String createColor(@RequestParam("nameColor") String name, Model model) {
        
        
        Color color = new Color();
        if (!colorRepository.findByNameColor(name).isEmpty()) {
            String message = "Màu " + name.toUpperCase() + " đã tồn tại!";
            model.addAttribute("message", message);
            return "redirect:/admin/bicycle-variations?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
        }
        color.setNameColor(name);
        colorRepository.saveAndFlush(color);
        return "redirect:/admin/bicycle-variations";
    }


    @GetMapping("/admin/bicycle-variations/remove/{id}")
    public String removeColor(Model model, @PathVariable("id") Integer id) {
        try {
            Color color = colorRepository.findById(id).orElse(null);
            if (color != null) {
                colorRepository.delete(color);
            }
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("errorMessage", "Không thể xóa màu vì đã có sản phẩm sử dụng màu này.");
            return "redirect:/admin/bicycle-variations?message=" + URLEncoder.encode("Không thể xóa màu vì đã có sản phẩm sử dụng màu này!", StandardCharsets.UTF_8);
        }
        return "redirect:/admin/bicycle-variations";
    }


    @PostMapping("/admin/bicycle-variations/create-type")
    public String createBicycleType(@RequestParam("bikeType") String type, Model model) {
        Category bicycleType = new Category();
        if (!categoryRepository.findByType(type).isEmpty()) {
            return "redirect:/admin/bicycle-variations?message=" + URLEncoder.encode("Loại xe " + type.toUpperCase() + " đã tồn tại!", StandardCharsets.UTF_8);
        }
        bicycleType.setType(type);
        categoryRepository.saveAndFlush(bicycleType);
        return "redirect:/admin/bicycle-variations";
    }

    @GetMapping("/admin/bicycle-variations/remove-type/{id}")
    public String removeCategory(Model model, @PathVariable("id") Integer id) {
        try {
            Category category = categoryRepository.findById(id).orElse(null);
            if (category != null) {
                categoryRepository.delete(category);
            }
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("errorMessage", "Không thể xóa loại xe vì đã có sản phẩm sử dụng loại xe này.");
            return "redirect:/admin/bicycle-variations?message=" + URLEncoder.encode("Không thể xóa loại xe vì đã có sản phẩm sử dụng loại xe này!", StandardCharsets.UTF_8);
        }
        return "redirect:/admin/bicycle-variations";
    }




    @PostMapping("/admin/bicycle-variations/create-size")
    public String createBicycleSize(@RequestParam("bikeSize") String size, Model model) {
        Size bicycleSize = new Size();
        if (!sizeRepository.findByNameSize(size).isEmpty()) {
            return "redirect:/admin/bicycle-variations?message=" + URLEncoder.encode("Kích cỡ " + size.toUpperCase() + " đã tồn tại!", StandardCharsets.UTF_8);
        }
        bicycleSize.setNameSize(size);
        sizeRepository.saveAndFlush(bicycleSize);
        return "redirect:/admin/bicycle-variations";
    }

    @GetMapping("/admin/bicycle-variations/remove-size/{id}")
    public String removeSize(Model model, @PathVariable("id") Integer id) {
        try {
            Size size = sizeRepository.findById(id).orElse(null);
            if (size != null) {
                sizeRepository.delete(size);
            }
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("errorMessage", "Không thể xóa kích cỡ vì đã có sản phẩm sử dụng kích cỡ này.");
            return "redirect:/admin/bicycle-variations?message=" + URLEncoder.encode("Không thể xóa kích cỡ vì đã có sản phẩm sử dụng kích cỡ này!", StandardCharsets.UTF_8);
        }
        return "redirect:/admin/bicycle-variations";
    }


}
