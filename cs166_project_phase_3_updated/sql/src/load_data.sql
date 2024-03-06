COPY Users
FROM '/home/csmajs/rdano001/CS166_Project3/cs166_project_phase_3_updated/data/users.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE users_userID_seq RESTART 101;

COPY Store
FROM '/home/csmajs/rdano001/CS166_Project3/cs166_project_phase_3_updated/data/stores.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Product
FROM '/home/csmajs/rdano001/CS166_Project3/cs166_project_phase_3_updated/data/products.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Warehouse
FROM '/home/csmajs/rdano001/CS166_Project3/cs166_project_phase_3_updated/data/warehouse.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Orders
FROM '/home/csmajs/rdano001/CS166_Project3/cs166_project_phase_3_updated/data/orders.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE orders_orderNumber_seq RESTART 501;


COPY ProductSupplyRequests
FROM '/home/csmajs/rdano001/CS166_Project3/cs166_project_phase_3_updated/data/productSupplyRequests.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE productsupplyrequests_requestNumber_seq RESTART 11;

COPY ProductUpdates
FROM '/home/csmajs/rdano001/CS166_Project3/cs166_project_phase_3_updated/data/productUpdates.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE productupdates_updateNumber_seq RESTART 51;
