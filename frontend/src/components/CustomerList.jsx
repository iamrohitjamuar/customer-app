import React from "react";

function CustomerList({ customers }) {
  if (!customers || customers.length === 0) {
    return <p>No customers found.</p>;
  }

  return (
    <table border="1">
      <thead>
        <tr>
          <th>Id</th>
          <th>First Name</th>
          <th>Last Name</th>
          <th>DOB</th>
        </tr>
      </thead>

      <tbody>
        {customers.map((c) => (
          <tr key={c.id}>
            <td>{c.id}</td>
            <td>{c.firstName}</td>
            <td>{c.lastName}</td>
            <td>{c.dateOfBirth}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export default CustomerList;


