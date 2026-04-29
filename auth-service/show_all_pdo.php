<?php
try {
    $pdo1 = new PDO('mysql:host=127.0.0.1;dbname=auth_db', 'root', '');
    $stmt1 = $pdo1->query('SHOW TABLES');
    $tables1 = $stmt1->fetchAll(PDO::FETCH_COLUMN);

    $pdo2 = new PDO('mysql:host=127.0.0.1;dbname=inventory_assets', 'root', '');
    $stmt2 = $pdo2->query('SHOW TABLES');
    $tables2 = $stmt2->fetchAll(PDO::FETCH_COLUMN);

    file_put_contents('final_dbs.txt', "auth_db: " . implode(',', $tables1) . "\n" . "inventory_assets: " . implode(',', $tables2));
} catch (Exception $e) {
    file_put_contents('final_dbs.txt', 'Error: ' . $e->getMessage());
}

