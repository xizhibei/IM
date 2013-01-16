<?php
require_once 'Zend/Db/Table/Abstract.php';
class FileModel extends Zend_Db_Table_Abstract {
    protected $_name = 'file';
    
    public function getAvatar($fileid){
        if ($fileid != 0)
            return "/upload/" . $this->_db->fetchOne("select name from file while fileid = $fileid");
        else
            return "/upload/default.png";
    }
}
?>
