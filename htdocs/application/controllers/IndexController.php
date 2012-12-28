<?php

class IndexController extends Zend_Controller_Action {

    public function init() {
        //$this->_helper->viewRenderer->setNoRender();
    }

    public function indexAction() {
    	echo time() . "<br>";
    	
        $time = new Zend_Date();
        $time->setHour(10);
        $time->setMinute(10);
        $time->setSecond(0);
        $v = $time->toValue();
        echo $v . "<br>";
        
        echo date("Y:m:d H:i:s",$v);
    }

    public function regAction() {
        
    }

}

