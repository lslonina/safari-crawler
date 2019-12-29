import "regenerator-runtime/runtime";
import "core-js/stable";
import React, {Component} from 'react';
import {Link, withRouter} from 'react-router-dom';
import {Button, Container, Form, FormGroup, Input, Label} from 'reactstrap';
import AppNavbar from './AppNavbar';
import {instanceOf} from 'prop-types';
import {Cookies, withCookies} from 'react-cookie';

class BookEdit extends Component {
    static propTypes = {
        cookies: instanceOf(Cookies).isRequired
    };

    emptyItem = {
        identifier: '',
        title: '',
        authors: [''],
        description: '',
        pages: '',
        priority: ''
    };

    constructor(props) {
        super(props);
        const {cookies} = props;
        this.state = {
            item: this.emptyItem,
            csrfToken: cookies.get('XSRF-TOKEN')
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    async componentDidMount() {
        let id = this.props.match.params.id;
        if (id !== 'new') {
            try {
                const book = await (await fetch(`/api/books/${id}`, {credentials: 'include'})).json();
                this.setState({item: book});
            } catch (error) {
                this.props.history.push('/');
            }
        }
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let item = {...this.state.item};
        item[name] = value;
        this.setState({item});
    }

    async handleSubmit(event) {
        event.preventDefault();
        const {item, csrfToken} = this.state;

        await fetch('/api/books', {
            method: (item.id) ? 'PUT' : 'POST',
            headers: {
                'X-XSRF-TOKEN': this.state.csrfToken,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(item),
            credentials: 'include'
        });
        this.props.history.push('/books');
    }

    render() {
        const {item} = this.state;
        const title = <h2>{item.id ? 'Edit Book' : 'Add Book'}</h2>;

        return <div>
            <AppNavbar/>
            <Container>
                {title}
                <Form onSubmit={this.handleSubmit}>
                    <FormGroup>
                        <Label for="name">Name</Label>
                        <Input type="text" name="name" id="name" value={item.title || ''}
                               onChange={this.handleChange} autoComplete="name"/>
                    </FormGroup>
                    <FormGroup>
                        <Label for="address">Address</Label>
                        <Input type="text" name="address" id="address" value={item.archive_id || ''}
                               onChange={this.handleChange} autoComplete="address-level1"/>
                    </FormGroup>
                    <FormGroup>
                        <Label for="city">City</Label>
                        <Input type="text" name="city" id="city" value={item.id || ''}
                               onChange={this.handleChange} autoComplete="address-level1"/>
                    </FormGroup>
                    {/*<div className="row">*/}
                    {/*    <FormGroup className="col-md-4 mb-3">*/}
                    {/*        <Label for="stateOrProvince">State/Province</Label>*/}
                    {/*        <Input type="text" name="stateOrProvince" id="stateOrProvince"*/}
                    {/*               value={item.stateOrProvince || ''}*/}
                    {/*               onChange={this.handleChange} autoComplete="address-level1"/>*/}
                    {/*    </FormGroup>*/}
                    {/*    <FormGroup className="col-md-5 mb-3">*/}
                    {/*        <Label for="country">Country</Label>*/}
                    {/*        <Input type="text" name="country" id="country" value={item.country || ''}*/}
                    {/*               onChange={this.handleChange} autoComplete="address-level1"/>*/}
                    {/*    </FormGroup>*/}
                    {/*    <FormGroup className="col-md-3 mb-3">*/}
                    {/*        <Label for="country">Postal Code</Label>*/}
                    {/*        <Input type="text" name="postalCode" id="postalCode" value={item.postalCode || ''}*/}
                    {/*               onChange={this.handleChange} autoComplete="address-level1"/>*/}
                    {/*    </FormGroup>*/}
                    {/*</div>*/}
                    <FormGroup>
                        <Button color="primary" type="submit">Save</Button>{' '}
                        <Button color="secondary" tag={Link} to="/books">Cancel</Button>
                    </FormGroup>
                </Form>
            </Container>
        </div>
    }
}

export default withCookies(withRouter(BookEdit));