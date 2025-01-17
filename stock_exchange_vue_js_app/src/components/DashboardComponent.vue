<template>
  <div>
    <h1>Stock Market Dashboard</h1>
    <h2>Available Stocks</h2>
    <ul>
      <li v-for="stock in stocks" :key="stock.ticker">
        {{ stock.ticker }} - ${{ stock.price.toFixed(2) }}
      </li>
    </ul>

    <h2>User Portfolio</h2>
    <ul>
      <li v-for="stock in stocks" :key="stock.ticker">
        {{ stock.ticker }}: {{ userStocks[stock.ticker] || 0 }} shares
      </li>
    </ul>

    <form @submit.prevent="placeOrder">
      <h3>Place an Order</h3>
      <select v-model="selectedStock">
        <option v-for="stock in stocks" :value="stock.ticker" :key="stock.ticker">
          {{ stock.ticker }}
        </option>
      </select>
      <input v-model="quantity" type="number" placeholder="Quantity" />
      <input v-model="price" type="number" placeholder="Price" />
      <select v-model="action">
        <option value="BUY">Buy</option>
        <option value="SELL">Sell</option>
      </select>
      <button type="submit">Place Order</button>
    </form>

    <h2>Live Orders</h2>
    <ul>
      <li v-for="order in liveOrders" :key="order.id">
        {{ order.intention }} {{ order.quantity }} shares of {{ order.stock }} @ ${{ order.price }}
      </li>
    </ul>
  </div>
</template>


<script>
import axios from "axios";

export default {
  data() {
    return {
      stocks: [], // Holds the list of stocks
      userStocks: {}, // Holds the user's portfolio stocks
      liveOrders: [], // Holds the list of live orders
      selectedStock: "", // Selected stock ticker for placing an order
      quantity: 0, // Quantity of the stock to buy/sell
      price: 0, // Price for the stock order
      action: "BUY", // Action type: BUY or SELL
      userId: 1, // User ID (hardcoded for now)
    };
  },
  async created() {
    // Fetch stocks, user portfolio, and live orders when the component is created
    await this.fetchStocks();
    await this.fetchUserStocks();
    await this.fetchLiveOrders();
  },
  methods: {
    async fetchStocks() {
      try {
        const response = await axios.get("http://localhost:9090/stocks");
        this.stocks = response.data; // Save fetched stock data
      } catch (error) {
        console.error("Failed to fetch stocks:", error);
        alert("Could not fetch stocks. Please try again later.");
      }
    },
    async fetchUserStocks() {
      try {
        const response = await axios.get(`http://localhost:8080/api/users/${this.userId}/stocks`);
        const userStocks = response.data; // User's actual stocks

        // Initialize userStocks with all available stocks defaulting to 0
        this.userStocks = {};
        this.stocks.forEach(stock => {
          this.userStocks[stock.ticker] = userStocks[stock.ticker] || 0;
        });
      } catch (error) {
        console.error("Failed to fetch user stocks:", error);
        alert("Could not fetch user portfolio. Please try again later.");
      }
    },
    async fetchLiveOrders() {
      try {
        const response = await axios.get("http://localhost:9090/orders/live");
        this.liveOrders = response.data; // Save fetched live orders data
      } catch (error) {
        console.error("Failed to fetch live orders:", error);
        alert("Could not fetch live orders. Please try again later.");
      }
    },
    async placeOrder() {
      if (this.quantity <= 0 || this.price <= 0) {
        alert("Quantity and Price must be positive numbers.");
        return;
      }

      try {
        await axios.post("http://localhost:9090/addTrade", {
          stock: this.selectedStock,
          quantity: this.quantity,
          price: this.price,
          action: this.action,
        });
        alert("Order placed successfully!");
        await this.fetchLiveOrders(); // Refresh the live orders list after placing an order
      } catch (error) {
        console.error("Failed to place order:", error);
        alert("Failed to place order. Please try again later.");
      }
    },
  },
};
</script>