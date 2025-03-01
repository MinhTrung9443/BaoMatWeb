package vn.iotstar.controller.vendor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Vendor")
public class VendorController {
    @GetMapping("")
    public String home() {
        return "Vendor/index";
    }
}
