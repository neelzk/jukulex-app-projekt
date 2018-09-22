<?php
    function dieWithHttpMsg($msg) {
        echo "$msg\n";
        die();
    }

    require_once('db.php');

    $error = NULL;
    if (!isset($_POST["username"])) {
        $error['username'] = 'Bitte Benutzername angeben.';
    }
    if (!isset($_POST["password"])) {
        $error['password'] = 'Bitte Passwort angeben.';
    }
    if (!isset($_POST["email"])) {
        $error['email'] = 'Bitte E-Mail angeben.';
    }

    if ($error != NULL) {
        $errorMsg = "";
        foreach ($error as $key => $value) {
            $errorMsg .= "Error: $value ";
        }
        diewithHttpsMsg($errorMsg);
    }

    $username = $mysqli->real_escape_string($_POST["username"]);
    $password = $mysqli->real_escape_string($_POST["password"]);
    $email = $mysqli->real_escape_string($_POST["email"]);

    $hash = password_hash($password, PASSWORD_DEFAULT);

    /*
    if (password_verify('rasmuslerdorf', $hash)) {
        echo 'Password is valid!';
    } else {
        echo 'Invalid password.';
    }
    */

    $sql  = "INSERT INTO `users` (`id`, `gid`, `name`, `email`, `password`) VALUES (NULL, '0', '$username', '$email', '$hash')";

    $res = $mysqli->query($sql);
    if (!$res) {
        dieWithHttpMsg($mysqli->error);
    }
?>
