use supply

db.createUser(
  {
    user: 'supply',
    pwd: 'supply',
    roles: ["readWrite", "dbAdmin"]
  }
)
