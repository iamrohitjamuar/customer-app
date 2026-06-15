import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchCustomers } from "./store/customersSlice";
import CustomerForm from "./components/CustomerForm";
import CustomerList from "./components/CustomerList";

function App() {
  const dispatch = useDispatch();

  const customersState = useSelector((s) => s.customers);
  const { items: customers, loading, error } = customersState;

  useEffect(() => {
    dispatch(fetchCustomers());
  }, [dispatch]);

  return (
    <div className="container">
      <h2>Customer Management</h2>

      <CustomerForm />

      {loading ? <p>Loading customers...</p> : null}
      {error ? (
        typeof error === "string" ? (
          <p style={{ color: "red" }}>{error}</p>
        ) : (
          <div style={{ color: "red" }}>
            {Object.values(error).map((m, i) => (
              <p key={i}>{m}</p>
            ))}
          </div>
        )
      ) : null}

      <hr />

      <CustomerList customers={customers} />
    </div>
  );
}

export default App;


