<?php
$f = fopen('C:\\xampp\\tmp\\donnees.csv', 'r');
for ($i = 0; $i < 12; $i++) {
    $row = fgetcsv($f, 0, ';');
    if ($i == 10) {
        echo "Row 10 col 0 hex: " . bin2hex((string)$row[0]) . "\n";
        echo "Row 10 col 0 str: " . $row[0] . "\n";
    }
}
fclose($f);

