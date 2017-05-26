<?php

class User_info {

    private $conn;

    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $db = new DB_connect();
        $this->conn = $db->connect();
    }

    // destructor
    function __destruct() {

    }

    /**
     * Storing new user
     * returns user details
     */
    public function StoreUserInfo($username, $name, $password, $perfil, $age) {
                $stmt = $this->conn->prepare("INSERT INTO usuario(username, name, password, perfil, age) VALUES(?, ?, ?, ?, ?)");
        $stmt->bind_param("sssss", $username, $name, $password, $perfil, $age);
        $result = $stmt->execute();
        $stmt->close();

        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM usuario WHERE username = ?");
            $stmt->bind_param("s", $username);
            $stmt->execute();
            $stmt-> bind_result($token1,$token2,$token3,$token4,$token5);
            while ( $stmt-> fetch() ) {
               $user["username"] = $token1;
               $user["name"] = $token2;
               $user["password"] = $token3;
               $user["perfil"] = $token4;
               $user["age"] = $token5;
            }
            $stmt->close();
            return $user;
        } else {
          return false;
        }
    }

    /**
     * Get user by email and password
     */
    public function VerifyUserAuthentication($username, $password) {

        $stmt = $this->conn->prepare("SELECT * FROM usuario WHERE username = ?");

        $stmt->bind_param("s", $username);

        if ($stmt->execute()) {
            $stmt-> bind_result($token1,$token2,$token3,$token4,$token5);
            while ( $stmt-> fetch() ) {
               $user["username"] = $token1;
               $user["name"] = $token2;
               $user["password"] = $token3;
               $user["perfil"] = $token4;
               $user["age"] = $token5;
            }
            $stmt->close();
            $encrypted_password = $token3;
            if ($encrypted_password == $password) {
                // user authentication details are correct
                return $user;
            }
        } else {
            return NULL;
        }
    }

    /**
     * Check user is existed or not
     */
    public function CheckExistingUser($username) {
        $stmt = $this->conn->prepare("SELECT username from usuario WHERE username = ?");
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $stmt->store_result();
        if ($stmt->num_rows > 0) {
            // user existed 
            $stmt->close();
            return true;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
    }

    /**
     * Encrypting password
     * param password
     * returns salt and encrypted password
     *
    *public function hashFunction($password) {

    *    $salt = sha1(rand());
    *    $salt = substr($salt, 0, 10);
    *    $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
    *    $hash = array("salt" => $salt, "encrypted" => $encrypted);
    *    return $hash;
    *}
    */

    /**
     * Decrypting password
     * param salt, password
     * returns hash string
     
    *public function checkHashFunction($salt, $password) {
    *    $hash = base64_encode(sha1($password . $salt, true) . $salt);
    *    return $hash;
    *}
*/
}

?>