<?php
        require_once("db.php");

        $jsonArr = array();

        $res = $mysqli->query("SELECT * FROM markers");

        while ($row = $res->fetch_assoc()) {
                $arrEl["text"] = $row["text"];
                $arrEl["latitude"] = $row["latitude"];
                $arrEl["longitude"] = $row["longitude"];
                $jsonArr[] = $arrEl;
        }

        echo json_encode($jsonArr);

?>
