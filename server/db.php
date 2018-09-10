<?php
        $mysqli = new mysqli("localhost", "phpmyadmin", "password", "juzapp");

        if ($mysqli->connect_errno) {
                header('HTTP/1.0 500 Internal Server Error');
                die();
        }
?>
