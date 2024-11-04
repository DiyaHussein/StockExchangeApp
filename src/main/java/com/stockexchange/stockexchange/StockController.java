package com.stockexchange.stockexchange;

import com.stockexchange.stockexchange.Order;
import com.stockexchange.stockexchange.Stock;
import com.stockexchange.stockexchange.StockExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StockController {

    @Autowired
    private StockExchangeService stockExchangeService;

    @GetMapping("/stocks")
    public List<Stock> getAllStocks() {
        return stockExchangeService.getStocks();
    }

    @GetMapping("/balance/{userId}")
    public double getUserBalance(@PathVariable String userId) {
        return stockExchangeService.getBalance(userId);
    }

    @PostMapping("/orders")
    public String placeOrder(@RequestBody Order order) {
        System.out.println("im here");
        return stockExchangeService.placeOrder(order);
    }
}
