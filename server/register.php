<?php
	function dieWithHttpMsg($msg) {

		die();
	}

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

	$username = mysqli_real_escape_string($_POST["username"]);
	$password = mysqli_real_escape_string($_POST["password"]);
	$email = mysqli_real_escape_string($_POST["email"]);

	$sql = 'SELECT * FROM `users` WHERE `name` = \"'.$username.'\"';
	if ($res = mysqli->query($sql)) {
		dieWithHttpMsg("Error: Benutzer existiert bereits.");
	}

	$hash = password_hash($password), PASSWORD_DEFAULT);

	/*
	if (password_verify('rasmuslerdorf', $hash)) {
		echo 'Password is valid!';
	} else {
		echo 'Invalid password.';
	}
	*/

	$sql  = 'INSERT INTO `users` (`id`, `name`, `email`, `password`) VALUES (NULL, \''.$username.'\', \''.$email.'\', \''.$hash.'\')';
        $res = $mysqli->query($sql);

?>
