<template>
  <div class="dashboard">
    <!-- Panel for User Stocks and Order Form -->
    <div class="user-panel">
      <div class="user-stocks">
        <h3>Your Stocks</h3>
        <ul>
          <li v-for="(quantity, stock) in userStocks" :key="stock">
            {{ stock }}: {{ quantity }}
          </li>
        </ul>
      </div>

      <div class="order-form">
        <h3>Place an Order</h3>
        <select v-model="selectedStock">
          <option v-for="stock in stockOptions" :key="stock" :value="stock">
            {{ stock }}
          </option>
        </select>
        <input type="number" v-model="orderQuantity" placeholder="Quantity" />
        <input type="number" v-model="orderPrice" placeholder="Price" />
        <button @click="placeBuyOrder">Buy</button>
        <button @click="placeSellOrder">Sell</button>
      </div>
    </div>

    <!-- Panels for Buy and Sell Orders -->
    <div class="orders">
      <div class="buy-orders">
        <h3>Buy Orders</h3>
        <ul>
          <li v-for="order in buyOrders" :key="order.id">
            BUY {{ order.quantity }} shares of {{ order.stock }} at the price of {{ order.price }}USD by {{ order.user }}
            <button @click="executeSellOrder(order)">Sell</button>
          </li>
        </ul>
      </div>

      <div class="sell-orders">
        <h3>Sell Orders</h3>
        <ul>
          <li v-for="order in sellOrders" :key="order.id">
            SELL {{ order.quantity }} shares of {{ order.stock }} at the price of {{ order.price }}USD by {{ order.user }}
            <button @click="executeBuyOrder(order)">Buy</button>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      userStocks: {}, // Retrieved from API
      stockOptions: ["AAPL", "MSFT", "AMZN", "GOOGL", "TSLA", "FB", "NFLX"],
      selectedStock: "AAPL",
      orderQuantity: 0,
      orderPrice: 0,
      buyOrders: [], // Retrieved from API
      sellOrders: [], // Retrieved from API
    };
  },
  methods: {
    fetchUserStocks() {
      const userId = localStorage.getItem('userId'); // Retrieve user ID from localStorage
      if (!userId) {
        console.error('User ID not found');
        return;
      }

      axios.get(`http://localhost:8080/api/users/${userId}/stocks`)
          .then(response => {
            this.userStocks = response.data;
          })
          .catch(err => {
            console.error('Failed to fetch user stocks:', err);
          });
    },
    fetchOrders() {
      // Call API to fetch buy and sell orders
      axios.get('http://localhost:8080/api/orders')
          .then(response => {
            this.buyOrders = response.data.buyOrders;
            this.sellOrders = response.data.sellOrders;
          })
          .catch(err => {
            console.error('Failed to fetch orders:', err);
          });
    },
    placeBuyOrder() {
      // Validate user has enough balance, then send API call
      axios.post('/http://localhost:8080/api/orders/buy', {
        stock: this.selectedStock,
        quantity: this.orderQuantity,
        price: this.orderPrice,
      })
          .then(() => {
            this.fetchOrders(); // Refresh orders
          })
          .catch(err => {
            console.error('Failed to place buy order:', err);
          });
    },
    placeSellOrder() {
      // Validate user has enough stock, then send API call
      axios.post('http://localhost:8080/api/orders/sell', {
        stock: this.selectedStock,
        quantity: this.orderQuantity,
        price: this.orderPrice,
      })
          .then(() => {
            this.fetchOrders(); // Refresh orders
          })
          .catch(err => {
            console.error('Failed to place sell order:', err);
          });
    },
    executeBuyOrder(order) {
      // Call API to execute buy order
      axios.post(`http://localhost:8080/api/orders/executeBuy/${order.id}`)
          .then(() => {
            this.fetchOrders(); // Refresh orders
          })
          .catch(err => {
            console.error('Failed to execute buy order:', err);
          });
    },
    executeSellOrder(order) {
      // Call API to execute sell order
      axios.post(`http://localhost:8080/api/orders/executeSell/${order.id}`)
          .then(() => {
            this.fetchOrders(); // Refresh orders
          })
          .catch(err => {
            console.error('Failed to execute sell order:', err);
          });
    },
  },
  mounted() {
    this.fetchUserStocks();
    this.fetchOrders();
  },
};
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
}

.user-panel {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.orders {
  display: flex;
  justify-content: space-between;
}

.buy-orders, .sell-orders {
  width: 48%;
}
</style>
