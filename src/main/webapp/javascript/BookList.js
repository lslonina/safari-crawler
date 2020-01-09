import React, {Component} from 'react';
import {Button, ButtonGroup, Container, Table} from 'reactstrap';
import {Link, withRouter} from 'react-router-dom';
import {instanceOf} from 'prop-types';
import {withCookies, Cookies} from 'react-cookie';
import queryString from 'query-string'
import Home from "./Home";
import parse from 'html-react-parser';
import Moment from "react-moment";

class BookList extends Component {
    static propTypes = {
        cookies: instanceOf(Cookies).isRequired
    };

    constructor(props) {
        super(props);
        const {cookies} = props;
        this.state = {books: [], csrfToken: cookies.get('XSRF-TOKEN'), isLoading: true};
        this.skip = this.skip.bind(this);
    }

    componentDidMount() {
        let query = this.props.location.search;
        const parsedQuery = queryString.parse(query);
        console.log('parsedQuery', parsedQuery)
        const filter = parsedQuery.filter;

        this.setState({isLoading: true});

        fetch('api/books' + query, {credentials: 'include'})
            .then(response => response.json())
            .then(data => this.setState({books: data, isLoading: false}))
            .catch(() => this.props.history.push('/'));
    }

    async skip(id, action) {
        await fetch(`/api/books/${id}/${action}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': this.state.csrfToken,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            credentials: 'include'
        }).then(() => {
            let updatedGroups = [...this.state.books].filter(i => i.identifier !== id);
            this.setState({books: updatedGroups});
        });
    }

    render() {
        const {books, isLoading} = this.state;

        if (isLoading) {
            return <p>Loading...</p>;
        }

        const groupList = books.map(book => {
            const desc = parse(book.description);
            const added = Date.parse(book.added)
            const published = Date.parse(book.published)
            return <tr key={book.identifier}>
                <td style={{whiteSpace: 'wrap'}}>{book.title}</td>
                <td style={{whiteSpace: 'pre-wrap'}}>{desc}</td>
                <td style={{whiteSpace: 'nowrap'}}><Moment format={"DD-MM-YYYY"}>{published}</Moment></td>
                <td style={{whiteSpace: 'nowrap'}}><Moment format={"DD-MM-YYYY"}>{added}</Moment></td>
                <td>{book.pages}</td>
                <td><img src={`/api/books/${book.identifier}/cover`} alt="Cover"/></td>
                <td>{book.priority}</td>
                <td>
                    <ButtonGroup>
                        <Button size="sm" color="primary" tag={Link} to={"/book/" + book.identifier}>Info</Button>
                        <Button size="sm" color="success" onClick={() => this.mark(book.identifier, 'select')}>Select</Button>
                        <Button size="sm" color="danger" onClick={() => this.mark(book.identifier, 'skip')}>Skip</Button>
                    </ButtonGroup>
                </td>
            </tr>
        });

        return (
            <div>
                <Home/>
                <Container fluid>
                    <div className="float-right">
                        <Button color="success" tag={Link} to="/books/new">Add Book</Button>
                    </div>
                    <h3>My Library</h3>
                    <Table className="mt-6">
                        <thead>
                        <tr>
                            <th width="20%">Title</th>
                            <th width="50%">Description</th>
                            <th width="2%">Published</th>
                            <th width="2%">Added</th>
                            <th width="5%">Pages</th>
                            <th width="6%">Cover</th>
                            <th width="5%">Priority</th>
                            <th width="10%">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {groupList}
                        </tbody>
                    </Table>
                </Container>
            </div>
        );
    }
}

export default withCookies(withRouter(BookList));