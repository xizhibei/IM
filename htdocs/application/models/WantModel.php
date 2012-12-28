<?php

/**
 * FriendModel
 *  
 * @author X
 * @version 
 */

require_once 'Zend/Db/Table/Abstract.php';


class WantModel extends Zend_Db_Table_Abstract {
	/**
	 * The default table name
	 */
	protected $_name = 'want';
	
        const Normal = 0;
        const Deleted = 1;
        const Expired = 2;
}
