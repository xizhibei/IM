<?php

/**
 * FriendModel
 *  
 * @author X
 * @version 
 */

require_once 'Zend/Db/Table/Abstract.php';


class ActivityModel extends Zend_Db_Table_Abstract {
	/**
	 * The default table name
	 */
	protected $_name = 'activity';
	
        public function GetActivityId($name){
            $sql = $this->_db->quoteInto("select aid from activity where name = ?", $name);
            return $this->_db->fetchOne($sql);
        }
}
