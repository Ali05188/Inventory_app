<?php $f = fopen("C:\\xampp\\tmp\\donnees.csv", "r"); for($i=0;$i<12;$i++){ $r = fgetcsv($f, 0, ";"); if ($i==10){ echo "Row 10 col 0 is: " . bin2hex($r[0]) . " / " . $r[0]; } }
