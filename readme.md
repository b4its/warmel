# Warung Mama Amel:
- Software 
    - Laragon
    - Xampp
    - Netbeans
    - Visual Studio Code
    - Powershell

- Requirements: 
    - Java
    - MYSQL

- Dependencies: 
    - jCalendar
    - mysql-connector
    - rs2xml
    - swingx-all-.1.6.4

- Get Started
    - Database Query:
        - ```mysql
            -- Create Database
            CREATE DATABASE IF NOT EXISTS warmel;
            USE warmel;

            -- Tabel Kategori
            CREATE TABLE kategori (
                idKategori INT(11) PRIMARY KEY AUTO_INCREMENT,
                namaKategori VARCHAR(100) NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB;

            -- Tabel Produk
            CREATE TABLE produk (
                idProduk INT(11) PRIMARY KEY AUTO_INCREMENT,
                namaProduk VARCHAR(255) NOT NULL,
                idKategori INT(11),
                hargaBeli DECIMAL(15,2),
                hargaJual DECIMAL(15,2),
                stok INT(11),
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                INDEX (idKategori),
                CONSTRAINT fk_produk_kategori 
                    FOREIGN KEY (idKategori) 
                    REFERENCES kategori(idKategori)
                    ON DELETE CASCADE
            ) ENGINE=InnoDB;

            -- Tabel Agen
            CREATE TABLE agen (
                idAgen INT(11) PRIMARY KEY AUTO_INCREMENT,
                namaAgen VARCHAR(255) NOT NULL,
                alamat TEXT,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB;

            -- Tabel Pembelian
            CREATE TABLE pembelian (
                idPembelian INT(11) PRIMARY KEY AUTO_INCREMENT,
                idAgen INT(11),
                keterangan TEXT,
                totalHarga DECIMAL(15,2),
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                INDEX (idAgen),
                CONSTRAINT fk_pembelian_agen FOREIGN KEY (idAgen) REFERENCES agen(idAgen)
            ) ENGINE=InnoDB;

            -- Tabel Detail Pembelian
            CREATE TABLE detail_pembelian (
                idDetailPembelian INT(11) PRIMARY KEY AUTO_INCREMENT,
                idPembelian INT(11),
                idProduk INT(11),
                jumlah INT,
                subtotal DECIMAL(15,2),
                INDEX (idPembelian),
                INDEX (idProduk),
                CONSTRAINT fk_detailpembelian_pembelian FOREIGN KEY (idPembelian) REFERENCES pembelian(idPembelian),
                CONSTRAINT fk_detailpembelian_produk FOREIGN KEY (idProduk) REFERENCES produk(idProduk)
            ) ENGINE=InnoDB;



            -- Tabel Penjualan
            CREATE TABLE penjualan (
                idPenjualan INT(11) PRIMARY KEY AUTO_INCREMENT,
                keterangan TEXT,
                totalHarga DECIMAL(15,2),
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB;

            -- Tabel Detail Penjualan (relasi ke penjualan dan produk)
            CREATE TABLE detail_penjualan (
                idDetailPenjualan INT(11) PRIMARY KEY AUTO_INCREMENT,
                idPenjualan INT(11),
                idProduk INT(11),
                jumlah INT,
                subtotal DECIMAL(15,2),
                INDEX (idPenjualan),
                INDEX (idProduk),
                CONSTRAINT fk_detailpenjualan_penjualan FOREIGN KEY (idPenjualan) 
                    REFERENCES penjualan(idPenjualan) ON DELETE CASCADE,
                CONSTRAINT fk_detailpenjualan_produk FOREIGN KEY (idProduk) 
                    REFERENCES produk(idProduk)
            ) ENGINE=InnoDB;
            ```
    - Dependencies pom.xml:
        - ``` xml
            <dependencies>
            <!--        dependency list start-->

                    <dependency>
                        <groupId>unknown.binary</groupId>
                        <artifactId>swingx-all</artifactId>
                        <version>1.6.4</version>
                    </dependency>
                    <dependency>
                        <groupId>unknown.binary</groupId>
                        <artifactId>jcalendar-1.4</artifactId>
                        <version>SNAPSHOT</version>
                    </dependency>
            <!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
                    <dependency>
                        <groupId>mysql</groupId>
                        <artifactId>mysql-connector-java</artifactId>
                        <version>8.0.33</version>
                    </dependency>
                    <dependency>
                        <groupId>commons-dbutils</groupId>
                        <artifactId>commons-dbutils</artifactId>
                        <version>1.7</version>
                    </dependency>
                    <dependency>
                    <groupId>net.proteanit.sql</groupId>
                    <artifactId>rs2xml</artifactId>
                    <version>1.0.6</version>
                    </dependency>
                    <!--        dependency list end -->
                    <dependency>
                        <groupId>org.netbeans.external</groupId>
                        <artifactId>AbsoluteLayout</artifactId>
                        <version>RELEASE220</version>
                    </dependency>
            </dependencies>
        ```