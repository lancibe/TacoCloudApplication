package tacos.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import tacos.Order;
import tacos.User;
import tacos.data.OrderRepository;
import tacos.data.UserRepository;

import javax.validation.Valid;
import java.security.Principal;


@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
public class OrderController {
    private OrderRepository orderRepo;
    private OrderProps orderProps;

    public OrderController(OrderRepository orderRepo, OrderProps orderProps)
    {
        this.orderRepo = orderRepo;
        this.orderProps = orderProps;
    }


    @GetMapping("/current")
    public String orderForm(@AuthenticationPrincipal User user, @ModelAttribute Order order)
    {
        if(order.getDeliveryName() == null)
            order.setDeliveryName(user.getFullname());
        if(order.getDeliveryStreet() == null)
            order.setDeliveryStreet(user.getStreet());
        if(order.getDeliveryCity() == null)
            order.setDeliveryCity(user.getCity());
        if(order.getDeliveryState() == null)
            order.setDeliveryState(user.getState());
        if(order.getDeliveryZip() == null)
            order.setDeliveryZip(user.getZip());

        return "orderForm";
    }

    @GetMapping
    public String ordersForUser(@AuthenticationPrincipal User user, Model model)
    {
        Pageable pageable = PageRequest.of(0, orderProps.getPageSize());
        model.addAttribute("orders",
                orderRepo.findByUserOrderByPlacedAtDesc(user, pageable));
        return "orderList";
    }

    @PostMapping
    public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, @AuthenticationPrincipal User user)
    {
        if(errors.hasErrors())
            return "orderForm";

        order.setUser(user);

        orderRepo.save(order);
        sessionStatus.setComplete();
        return "redirect:/";
    }
}
