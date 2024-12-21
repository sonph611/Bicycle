package com.bikestore.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.mail.internet.ParseException;
import jakarta.persistence.Tuple;
import jakarta.servlet.http.HttpServletRequest;

import com.bikestore.dto.OrderStatusCount;
import com.bikestore.dto.OrderSummary;
import com.bikestore.dto.ProductRevenueDTO;
import com.bikestore.model.*;
import com.bikestore.repository.OrderRepository;
import com.bikestore.service.OrderService;

@Controller
public class ReportOrderController {

	@Autowired
	OrderService orderService;
	@Autowired
	HttpServletRequest request;
	@Autowired
	OrderRepository orderRepository;

	@RequestMapping("/admin/report")
	public String showReport(Model model) throws ParseException {
		// Thống kê trạng thái đơn hàng
		List<OrderStatusCount> statusCounts = orderService.getOrderCountByStatus();
		model.addAttribute("statusCounts", statusCounts);

		// Tính tổng số lượng đơn hàng
		long totalOrders = statusCounts.stream().mapToLong(OrderStatusCount::getCount).sum();
		model.addAttribute("totalOrders", totalOrders);

		// Doanh thu trong ngày
		Date today = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String todayString = dateFormat.format(today);

		Date formattedToday = null;
		try {
			formattedToday = dateFormat.parse(todayString);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}

		if (formattedToday != null) {
			OrderSummary summaryToday = orderService.getTotalRevenueSummaryByDate(formattedToday);
			model.addAttribute("totalRevenueToday", summaryToday.getTotalRevenue());
			model.addAttribute("orderCountToday", summaryToday.getOrderCount());
		}

		// Doanh thu ngày hôm qua
		OrderSummary summaryYesterday = orderService.getTotalRevenueSummaryByYesterday();
		if (summaryYesterday != null) {
			long orderCountYesterday = summaryYesterday.getOrderCount();
			float totalRevenueYesterday = summaryYesterday.getTotalRevenue();
			model.addAttribute("orderCountYesterday", orderCountYesterday);
			model.addAttribute("totalRevenueYesterday", totalRevenueYesterday);
		}

		// Thống kê theo tháng
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH) + 1; // Tháng được đánh số từ 0
		OrderSummary summaryMonth = orderService.getTotalRevenueSummaryByMonth(month);
		model.addAttribute("totalMonth", summaryMonth.getTotalRevenue());
		model.addAttribute("orderCountMonth", summaryMonth.getOrderCount());

		// Thống kê theo năm
		int year = calendar.get(Calendar.YEAR);
		OrderSummary summaryYear = orderService.getTotalRevenueSummaryByYear(year);
		model.addAttribute("totalYear", summaryYear.getTotalRevenue());
		model.addAttribute("orderCountYear", summaryYear.getOrderCount());

		// Lấy danh sách 4 sản phẩm bán nhiều nhất
		List<ProductRevenueDTO> topProductRevenues = orderService.getTopProductRevenues();
		model.addAttribute("topProductRevenues", topProductRevenues);

		model.addAttribute("view", "Report.html");
		return "admin/fontend/Report";
	}

}
