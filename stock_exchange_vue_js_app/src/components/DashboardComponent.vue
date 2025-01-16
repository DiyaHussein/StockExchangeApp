<template>
  <div>
    <h1>Stock Market Dashboard</h1>
    <ul>
      <li v-for="stock in stocks" :key="stock.id">
        {{ stock.symbol }} - ${{ stock.price }}
      </li>
    </ul>
    <form @submit.prevent="placeOrder">
      <select v-model="selectedStock">
        <option v-for="stock in stocks" :value="stock.symbol" :key="stock.id">
          {{ stock.symbol }}
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
      stocks: [],
      liveOrders: [],
      selectedStock: "",
      quantity: 0,
      price: 0,
      action: "BUY",
    };
  },
  async created() {
    await this.fetchStocks();
    await this.fetchLiveOrders();
  },
  methods: {
    async fetchStocks() {
      try {
        const response = await axios.get("http://localhost:8080/stocks");
        this.stocks = response.data;
      } catch (error) {
        console.error("Failed to fetch stocks:", error);
      }
    },
    async fetchLiveOrders() {
      try {
        const response = await axios.get("http://localhost:8080/orders/live");
        this.liveOrders = response.data;
      } catch (error) {
        console.error("Failed to fetch live orders:", error);
      }
    },
    async placeOrder() {
      try {
        await axios.post("http://localhost:8080/orders", {
          stock: this.selectedStock,
          quantity: this.quantity,
          price: this.price,
          intention: this.action,
        });
        alert("Order placed successfully!");
        await this.fetchLiveOrders(); // Refresh live orders
      } catch (error) {
        console.error("Failed to place order:", error);
      }
    },
  },
};
</script>
