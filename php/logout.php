<?php
// 1. Iniciamos la sesión para poder acceder a ella
session_start();

// 2. Destruimos todas las variables de sesión
$_SESSION = array();

// 3. Destruimos la sesión en el servidor
session_destroy();

// 4. Enviamos al usuario de vuelta al login
header("Location: login.php");
exit();
?>