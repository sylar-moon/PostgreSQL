
        CREATE TABLE store (
                  id INT,
                  city VARCHAR(20) NOT NULL,
                  address VARCHAR(100) NOT NULL,
                  CONSTRAINT store_pk PRIMARY KEY (id)
                );

            CREATE TABLE type (
					id INT,
					name_type VARCHAR(40) NOT NULL,
					CONSTRAINT type_pk PRIMARY KEY (id)
                );
            CREATE INDEX name_type ON type (name_type);


            CREATE TABLE good (
					id INT,
					name_good VARCHAR(40) NOT NULL,
					type_id INT NOT NULL,
                    FOREIGN KEY(type_id) REFERENCES type(id),
                    CONSTRAINT good_pk PRIMARY KEY (id)
					);
            CREATE INDEX type_id ON good (type_id);

            CREATE TABLE store_good
                (store_id INT NOT NULL,
                good_id INT NOT NULL,
                good_quantity INT,
                FOREIGN KEY(store_id) REFERENCES store(id),
                FOREIGN KEY(good_id) REFERENCES good(id))
               ;
            CREATE INDEX store_id ON store_good (store_id);
            CREATE INDEX good_id ON store_good (good_id);
            CREATE INDEX good_quantity ON store_good (good_quantity);
