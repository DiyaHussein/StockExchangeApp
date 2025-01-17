<template>
  <div>
    <h1>Stock Exchange Dashboard</h1>

    <section>
      <h2>Available Stocks</h2>
      <ul>
        <li v-for="stock in stocks" :key="stock.ticker">
          {{ stock.ticker }} - {{ stock.price }}
        </li>
      </ul>
    </section>

    <section>
      <h2>Active Orders</h2>
      <ul>
        <li v-for="order in orders" :key="order.id">
          {{ order.intention }} {{ order.quantity }} shares of {{ order.stock }} @ {{ order.price }} (by User {{ order.userId }})
        </li>
      </ul>
    </section>

    <section>
      <h2>Place a New Order</h2>
      <form @submit.prevent="placeOrder">
        <label for="stock">Stock:</label>
        <input type="text" id="stock" v-model="newOrder.stock" required />

        <label for="quantity">Quantity:</label>
        <input type="number" id="quantity" v-model="newOrder.quantity" required />

        <label for="price">Price:</label>
        <input type="number" id="price" v-model="newOrder.price" required />

        <label for="action">Action:</label>
        <select id="action" v-model="newOrder.action">
          <option value="BUY">BUY</option>
          <option value="SELL">SELL</option>
        </select>

        <button type="submit">Submit Order</button>
      </form>
    </section>
  </div>
</template>

<script>
import axios from '../plugins/axios';

export default {
  data() {
    return {
      stocks: [],
      orders: [],
      newOrder: {
        userId: '', // Set this from the logged-in user's ID
        stock: '',
        quantity: 0,
        price: 0,
        action: 'BUY', // BUY or SELL
      },
    };
  },
  mounted() {
    this.fetchStocks();
    this.fetchOrders();
  },
  methods: {
    fetchStocks() {
      axios
          .get('/stocks')
          .then((response) => {
            this.stocks = response.data;
          })
          .catch((error) => console.error(error));
    },
    fetchOrders() {
      axios
          .get('/orders/live')
          .then((response) => {
            this.orders = response.data;
          })
          .catch((error) => console.error(error));
    },
    placeOrder() {
      axios
          .post('/addTrade', this.newOrder)
          .then(() => {
            alert('Order placed successfully!');
            this.fetchOrders(); // Refresh orders list after placing an order
          })
          .catch((error) => {
            console.error(error);
            alert('Failed to place order.');
          });
    },
  },
};
</script>

<style>
h1, h2 {
  text-align: center;
}

form {
  display: flex;
  flex-direction: column;
  max-width: 400px;
  margin: 0 auto;
}

form label {
  margin: 0.5em 0 0.2em;
}

form input,
form select,
form button {
  margin-bottom: 1em;
}
</style>