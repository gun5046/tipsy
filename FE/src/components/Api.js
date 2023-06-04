import axios from "axios";

const api = axios.create({
  baseURL: "http://i8d207.p.tipsy.io:8083",

});

export default api;