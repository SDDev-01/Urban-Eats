<?php
$host = "localhost";
$user = "root";
//puse contra en mi pc por eso me toca ponerla aca pero toca eliminarla en el sena
//$pass = "";
$pass = "15975303";
$db   = "UrbanEats";

$conexion = mysqli_connect($host, $user, $pass, $db);

// Verificamos si hay error
if (!$conexion) {
    die("Error de conexión: " . mysqli_connect_error());
}
?>