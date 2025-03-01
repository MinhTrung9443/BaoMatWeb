package vn.iotstar.controller.user;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.Person;
import vn.iotstar.entity.Voucher;
import vn.iotstar.repository.IOrderRepository;
import vn.iotstar.repository.IVoucherRepository;
import vn.iotstar.service.ICartService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.payment.VNPAYService;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VNPayController {
    @Autowired
    private VNPAYService vnPayService;
    @Autowired
    private IOrderRepository orderService;
    @Autowired
	private ICartService cartService;
    @Autowired
	private IVoucherRepository voucherService;
    @Autowired
	private IProductService productService;
    
    @GetMapping("/vnpay-payment-return")
    public String paymentCompleted(HttpServletRequest request, Model model,HttpSession session) {
    	Person user = (Person) session.getAttribute("user");
        int paymentStatus = vnPayService.orderReturn(request);
        Order order = (Order) session.getAttribute("checkingOutOrder");
        order.getPayment().setPaymentDate(LocalDateTime.now());
        Voucher voucher = (Voucher) session.getAttribute("orderVoucher");
        
        Integer check = (Integer)session.getAttribute("single");
        if (paymentStatus == 1) {
        	if (voucher != null)
        	{
    			voucherService.save(voucher);
        	}
        	else 
        	{
    			orderService.save(order);
        	}
        	
        	if (check != null)
        	{
        		productService.decreaseProductStock((int) session.getAttribute("productId"));
        	}
        	else 
        	{
        		cartService.deleteAllCartItem(user.getId());
        	}
            return "redirect:/User/Order";
        } else {
            return "redirect:/User";
        }
    }


    @PostMapping("/submitOrder")
    public String submidOrder(@RequestParam("amount") int orderTotal,
                              @RequestParam("orderInfo") String orderInfo,
                              HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(request, orderTotal, orderInfo, baseUrl);
        return "redirect:" + vnpayUrl;
    }


}