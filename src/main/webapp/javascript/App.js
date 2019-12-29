import React, {Component} from 'react';
import '../css/App.css';
import Home from './Home';
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom';
import GroupList from './GroupList';
import GroupEdit from './GroupEdit';
import {CookiesProvider} from 'react-cookie';

class App extends Component {
    render() {
        return (
            <CookiesProvider>
                <Router>
                    <Switch>
                        <Route path='/' exact={true} component={Home}/>
                        <Route path='/books' exact={true} component={GroupList}/>
                        <Route path='/books/:isbn' component={GroupEdit}/>
                    </Switch>
                </Router>
            </CookiesProvider>
        )
    }
}

export default App;