<?php
require __DIR__.'/vendor/autoload.php';
$lines = file(__DIR__.'/app/Jobs/ProcessAssetImport.php');
foreach(array_slice($lines, 45, 25) as $l) echo $l;



