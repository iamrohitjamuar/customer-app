import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { customersApi, getApiErrorMessage } from "../api/CustomersApi";

export const fetchCustomers = createAsyncThunk(
  "customers/fetchAll",
  async (_, { rejectWithValue }) => {
    try {
      return await customersApi.getAll();
    } catch (error) {
      return rejectWithValue(getApiErrorMessage(error));
    }
  }
);

export const createCustomer = createAsyncThunk(
  "customers/create",
  async (customer, { rejectWithValue }) => {
    try {
      return await customersApi.create(customer);
    } catch (error) {
      return rejectWithValue(getApiErrorMessage(error));
    }
  }
);

const customersSlice = createSlice({
  name: "customers",
    initialState: {
    items: [],
    loading: false,
    error: null, // can be string or object { field: message }
    creating: false,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchCustomers.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCustomers.fulfilled, (state, action) => {
        state.loading = false;
        state.items = Array.isArray(action.payload) ? action.payload : [];
      })
      .addCase(fetchCustomers.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || action.error?.message || "Failed to load customers";
      })
      .addCase(createCustomer.pending, (state) => {
        state.creating = true;
        state.error = null;
      })
      .addCase(createCustomer.fulfilled, (state) => {
        state.creating = false;
      })
      .addCase(createCustomer.rejected, (state, action) => {
        state.creating = false;
        state.error = action.payload || action.error?.message || "Failed to create customer";
      });
  },
});

export default customersSlice.reducer;
