<?php
$host = "localhost";
$user = "root";
$pass = "";
$db   = "urbaneats";

$conexion = mysqli_connect($host, $user, $pass, $db);

// Verificamos si hay error
if (!$conexion) {
    die("Error de conexión: " . mysqli_connect_error());
}
?>