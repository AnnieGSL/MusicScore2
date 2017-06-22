<?php
class DB_Connect {
	private $conn;
	//conectando a la Base de Datos
	public function connect(){
		require_once 'config.php';

		//conectando a mi DB mySql
		$this->conn = new mysqli(DB_HOST,DB_USER,DB_PASSWORD,DB_DATABASE);
		//retorna objeto de bases de datos
		return $this->conn;
	}
}
?>