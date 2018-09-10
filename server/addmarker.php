<?php
        if (!isset($_POST["text"])) {
                header('HTTP/1.0 500 Internal Server Error');
                die();
        }
        if (!isset($_POST["longitude"])) {
                header('HTTP/1.0 500 Internal Server Error');
                die();
        }
        if (!isset($_POST["latitude"])) {
                header('HTTP/1.0 500 Internal Server Error');
                die();
        }

        require_once("db.php");

        /*
        if (isset($_POST["user"] && isset($_POST["pass"]) {

        }
        */

        $text = $_POST["text"];
        $longitude = $_POST["longitude"];
        $latitude = $_POST["latitude"];

        $sql  = 'INSERT INTO `markers` (`id`, `text`, `longitude`, `latitude`) VALUES (NULL, \''.$text.'\', \''.$longitude.'\', \''.$latitude.'\')';
        $res = $mysqli->query($sql);

?>
