<?php
require_once 'User_info.php';
$db = new User_info();
 
// json response array
$response = array("error" => FALSE);
 
if (isset($_POST['username']) && isset($_POST['password'])) {
 
    // receiving the post params
    $username = $_POST['username'];
    $password = $_POST['password'];


    if($username != '' && $password != ''){
	 
	    // get the user by email and password
	    //Hasta aqui funciona
	    $user = $db->VerifyUserAuthentication($username, $password);
	 
	    if ($user != false) {
	        // use is found
	        $response["error"] = FALSE;
	        $response["user"]["username"] = $user["username"];
	        $response["user"]["name"] = $user["name"];
	        $response["user"]["password"] = $user["password"];
	        $response["user"]["perfil"] = $user["perfil"];
	        $response["user"]["age"] = $user["age"];
	        echo json_encode($response);
	    } else {
	        // user is not found with the credentials
	        $response["error"] = TRUE;
	        $response["error_msg"] = "Datos erróneos. Por favor, inténtero de nuevo!";
	        echo json_encode($response);
	    }
	}else {
	    // required post params is missing
	    $response["error"] = TRUE;
	    $response["error_msg"] = "Ingrése todos los campos!";
	    echo json_encode($response);
	}
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Ingrése todos los campos";
    echo json_encode($response);
}
?>