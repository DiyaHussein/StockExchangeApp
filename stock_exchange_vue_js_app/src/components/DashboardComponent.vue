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
        <select v-model="orderAction">
          <option value="BUY">Buy</option>
          <option value="SELL">Sell</option>
        </select>
        <button @click="placeTradeOrder">Submit Order</button>
      </div>
    </div>

    <!-- Panels for Buy and Sell Orders -->
    <div class="orders">
      <div class="buy-orders">
        <h3>Buy Orders</h3>
        <ul>
          <li v-for="order in buyOrders" :key="order.id">
            BUY {{ order.quantity }} shares of {{ order.stock }} at the price of {{ order.price }}USD by {{ order.user }}
            <button @click="executeTrade(order, 'SELL')">Sell</button>
          </li>
        </ul>
      </div>

      <div class="sell-orders">
        <h3>Sell Orders</h3>
        <ul>
          <li v-for="order in sellOrders" :key="order.id">
            SELL {{ order.quantity }} shares of {{ order.stock }} at the price of {{ order.price }}USD by {{ order.user }}
            <button @click="executeTrade(order, 'BUY')">Buy</button>
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
      userStocks: {}, // User's stocks
      stockOptions: ["AAPL", "MSFT", "AMZN", "GOOGL", "TSLA", "FB", "NFLX"],
      selectedStock: "AAPL",
      orderQuantity: 0,
      orderPrice: 0,
      orderAction: "BUY", // BUY or SELL
      buyOrders: [], // Live buy orders
      sellOrders: [], // Live sell orders
    };
  },
  methods: {
    fetchUserStocks() {
      const userId = localStorage.getItem('userId');
      if (!userId) {
        console.error('User ID not found');
        return;
      }

      axios
          .get(`http://localhost:8080/api/users/${userId}/stocks`)
          .then((response) => {
            this.userStocks = response.data;
          })
          .catch((err) => {
            console.error('Failed to fetch user stocks:', err);
          });
    },
    fetchOrders() {
      axios
          .get('http://localhost:9090/orders/live')
          .then((response) => {
            this.buyOrders = response.data.filter((order) => order.action === 'BUY');
            this.sellOrders = response.data.filter((order) => order.action === 'SELL');
          })
          .catch((err) => {
            console.error('Failed to fetch live orders:', err);
          });
    },
    placeTradeOrder() {
      const userId = localStorage.getItem('userId');
      if (!userId) {
        console.error('User ID not found');
        return;
      }

      const order = {
        userId,
        stock: this.selectedStock,
        quantity: this.orderQuantity,
        price: this.orderPrice,
        action: this.orderAction,
      };

      axios
          .post('http://localhost:9090/addTrade', order)
          .then(() => {
            this.fetchOrders(); // Refresh live orders
            this.fetchUserStocks(); // Refresh user stocks
          })
          .catch((err) => {
            console.error('Failed to place trade order:', err);
          });
    },
    executeTrade(order, action) {
      axios
          .post(`http://localhost:9090/addTrade`, {
            userId: localStorage.getItem('userId'),
            stock: order.stock,
            quantity: order.quantity,
            price: order.price,
            action,
          })
          .then(() => {
            this.fetchOrders(); // Refresh live orders
            this.fetchUserStocks(); // Refresh user stocks
          })
          .catch((err) => {
            console.error('Failed to execute trade:', err);
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

.buy-orders,
.sell-orders {
  width: 48%;
}
</style>
