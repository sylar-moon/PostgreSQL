
        CREATE TABLE stores (
                  id INT,
                  city VARCHAR(20) NOT NULL,
                  address VARCHAR(100) NOT NULL,
                  CONSTRAINT stores_pk PRIMARY KEY (id)
                );

        CREATE TABLE brands (
                  id INT,
                  name_brand VARCHAR(40) NOT NULL,
                  CONSTRAINT brands_pk PRIMARY KEY (id)
                );

            CREATE TABLE types (
					id INT,
					name_type VARCHAR(40) NOT NULL,
					CONSTRAINT types_pk PRIMARY KEY (id)
                );
            CREATE INDEX name_type ON types (name_type);


            CREATE TABLE goods (
					id INT,
					name_goods VARCHAR(40) NOT NULL,
					types_id INT NOT NULL,
                    brands_id INT NOT NULL,
                    FOREIGN KEY(types_id) REFERENCES types(id),
                    FOREIGN KEY(brands_id) REFERENCES brands(id),
                    CONSTRAINT goods_pk PRIMARY KEY (id)
					);
            CREATE INDEX types_id ON goods (types_id);

            CREATE TABLE store_good
                (id serial,
                stores_id INT NOT NULL,
                goods_id INT NOT NULL,
                goods_quantity INT,
                FOREIGN KEY(stores_id) REFERENCES stores(id),
                FOREIGN KEY(goods_id) REFERENCES goods(id),
                CONSTRAINT store_good_pk PRIMARY KEY (id))
               ;
            CREATE INDEX stores_id ON store_good (stores_id);
            CREATE INDEX goods_id ON store_good (goods_id);
            CREATE INDEX goods_quantity ON store_good (goods_quantity);
