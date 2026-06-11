<?php
session_start();
require 'db.php';

$email = $_POST['email'];
$password = $_POST['password'];

// 1. Buscamos el usuario en la base de datos
$sql = "SELECT Nombres, password FROM usuario WHERE CorreoElectronico = ?";
$stmt = mysqli_prepare($conexion, $sql);
mysqli_stmt_bind_param($stmt, "s", $email);
mysqli_stmt_execute($stmt);
$resultado = mysqli_stmt_get_result($stmt);

// 2. ¿Existe el usuario?
if ($usuario = mysqli_fetch_assoc($resultado)) {
    // 3. Verificamos la contraseña
    if ($password == $usuario['password']) {
        $_SESSION['usuario'] = $usuario['Nombres']; // Guardamos el nombre real
        header("Location: catalogo.html");
        exit();
    } else {
        die("Contraseña incorrecta");
    }
} else {
    // Si no existe, el proceso se detiene y no entra
    die("Usuario no encontrado");
}
?>