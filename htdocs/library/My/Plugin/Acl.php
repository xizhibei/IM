<?php

class My_Plugin_Acl extends Zend_Controller_Plugin_Abstract {

    private $_auth;
    private $_acl;

    public function __construct(Zend_Auth $auth, Zend_Acl $acl) {
        $this->_auth = $auth;
        $this->_acl = $acl;
    }

    public function preDispatch(Zend_Controller_Request_Abstract $request) {
        $request = $this->getRequest();
        $module = $request->getModuleName();
        $controller = $request->getControllerName();
        $action = $request->getActionName();
        $resource = $module . '_' . $controller;
        $acl = $this->_acl;

//        if ($this->_auth->hasIdentity()) {
//            $role = 'user';
//        }else
//            $role = 'guest';
//        $u = (array) $this->_auth->getStorage()->read();
//        echo $controller;
//        if (!isset($u['uid']) && ((($controller != "user" && $action != "login") && $controller != "index"))) {
//            $request->setControllerName("error");
//            $request->setActionName("pleaselogin");
//        }
//        if ($acl->has($resource)) {
//            if (!$acl->isAllowed($role, $resource, $action)) {
//                switch ($role) {
//                    case 'guest':
//                        //$module = 
//                        $controller = 'user';
//                        $action = 'login';
//                        break;
//                    default :
//                        break;
//                }
//            }
//        }
        //parent::preDispatch($request);
    }

    public function setUser($data) {
        $this->_auth->getStorage()->write((object) $data);
    }

    public function getUser() {
        return (array) $this->_auth->getStorage()->read();
    }

}

?>
