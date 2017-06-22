<?php
require_once 'User_info.php';
$db = new User_info();

$response = array("error" => FALSE);

if(isset($_POST['username']) && isset($_POST['name']) && isset($_POST['password'])  && isset($_POST['perfil']) && isset($_POST['age'])){
	// receiving the post params
	$username = $_POST['username'];
	$name = $_POST['name'];
	$password = $_POST['password'];
	$perfil = $_POST['perfil'];
	$age = $_POST['age'];
	if($name != '' && $username != '' && $password != '' && $perfil != '' && $age != ''){
		
	    // check if user is already existed with the same email
	    if ($db->CheckExistingUser($username)) {
	        // user already existed
	        $response["error"] = TRUE;
	        $response["error_msg"] = "Usuario ya existente: " . $username;
	        echo json_encode($response);
	    } else {
	        // create a new user
	        $user = $db->StoreUserInfo($username, $name, $password, $perfil, $age);
	        if ($user) {
	            // user stored successfully
	            $response["error"] = FALSE;
	            $response["user"]["username"] = $user["username"];
	            $response["user"]["name"] = $user["name"];
	            $response["user"]["password"] = $user["password"];
	            $response["user"]["perfil"] = $user["perfil"];
	            $response["user"]["age"] = $user["age"];
	            echo json_encode($response);
	        } else {
	            // user failed to store
	            $response["error"] = TRUE; 
	            $response["error_msg"] = "Error desconocido ocurrió durante el registro!";
	            echo json_encode($response);
	        }
	     }
     }else {
     	$response["error"] = TRUE;
    	$response["error_msg"] = "Requiere todos los campos!";
    	echo json_encode($response);
	}
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Requiere todos los campos!";
    echo json_encode($response);
}
?>