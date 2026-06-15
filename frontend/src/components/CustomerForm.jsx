import React, { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { createCustomer, fetchCustomers } from "../store/customersSlice";

function CustomerForm() {
  const dispatch = useDispatch();
  const creating = useSelector((s) => s.customers.creating);
  const error = useSelector((s) => s.customers.error);

  const [customer, setCustomer] = useState({
    firstName: "",
    lastName: "",
    dateOfBirth: "",
  });

  const submit = async (e) => {
    e.preventDefault();

    try {
      await dispatch(createCustomer(customer)).unwrap();
      // clear any previous errors by refetching
      dispatch(fetchCustomers());
    } catch {
      return;
    }

    setCustomer({
      firstName: "",
      lastName: "",
      dateOfBirth: "",
    });
  };

  return (
    <form onSubmit={submit}>
      {error ? (
        typeof error === "string" ? (
          <div style={{ color: "red", marginBottom: 8 }}>{error}</div>
        ) : (
          <div style={{ color: "red", marginBottom: 8 }}>
            {Object.values(error).map((m, i) => (
              <div key={i}>{m}</div>
            ))}
          </div>
        )
      ) : null}
      <input
       label="First Name"
        placeholder="First Name"
        value={customer.firstName}
        onChange={(e) =>
          setCustomer({
            ...customer,
            firstName: e.target.value,
          })
        }
      />

      <input
      label="Last Name"
        placeholder="Last Name"
        value={customer.lastName}
        onChange={(e) =>
          setCustomer({
            ...customer,
            lastName: e.target.value,
          })
        }
      />

      <input
        type="date"
        label="Date of Birth"
        value={customer.dateOfBirth}
        onChange={(e) =>
          setCustomer({
            ...customer,
            dateOfBirth: e.target.value,
          })
        }
      />

      <button type="submit" disabled={creating}>
        {creating ? "Saving..." : "Save"}
      </button>
    </form>
  );
}

export default CustomerForm;

