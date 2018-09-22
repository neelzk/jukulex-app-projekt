<?php
    if (!isset($_POST["text"])) {
        header('HTTP/1.0 500 Internal Server Error');
        die();
    }
    if (!isset($_POST["latitude"])) {
        header('HTTP/1.0 500 Internal Server Error');
        die();
    }
    if (!isset($_POST["longitude"])) {
        header('HTTP/1.0 500 Internal Server Error');
        die();
    }

    require_once("db.php");

    /*
    if (isset($_POST["user"] && isset($_POST["pass"]) {

    }
    */

    $text = $mysqli->real_escape_string($_POST["text"]);
    $latitude = $mysqli->real_escape_string($_POST["latitude"]);
    $longitude = $mysqli->real_escape_string($_POST["longitude"]);

    $sql  = "INSERT INTO `markers` (`id`, `text`, `latitude`, `longitude`) VALUES (NULL, '$text', '$latitude', '$longitude')";
    $res = $mysqli->query($sql);

?>
