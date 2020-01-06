import React, {Component} from 'react';
import {Button, ButtonGroup, Container, Table} from 'reactstrap';
import {Link, withRouter} from 'react-router-dom';
import {instanceOf} from 'prop-types';
import {withCookies, Cookies} from 'react-cookie';
import queryString from 'query-string'
import Home from "./Home";

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

    async skip(id) {
        await fetch(`/api/books/${id}/skip`, {
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
            const identifier = `${book.identifier || ''}`;
            return <tr key={book.identifier}>
                <td style={{whiteSpace: 'nowrap'}}>{book.title}</td>
                <td style={{whiteSpace: 'pre-wrap'}}>{book.description}</td>
                <td>{book.pages}</td>
                <td><img src={`/api/books/${book.identifier}/cover`} alt="Cover"/></td>
                <td>{book.priority}</td>
                {/*<td>{book.events.map(event => {*/}
                {/*    return <div key={event.id}>{new Intl.DateTimeFormat('en-US', {*/}
                {/*        year: 'numeric',*/}
                {/*        month: 'long',*/}
                {/*        day: '2-digit'*/}
                {/*    }).format(new Date(event.date))}: {event.title}</div>*/}
                {/*})}</td>*/}
                <td>
                    <ButtonGroup>
                        <Button size="sm" color="primary" tag={Link} to={"/book/" + book.identifier}>Edit</Button>
                        <Button size="sm" color="danger" onClick={() => this.skip(book.identifier)}>Delete</Button>
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
                    <Table className="mt-4">
                        <thead>
                        <tr>
                            <th width="20%">Title</th>
                            <th width="20%">Description</th>
                            <th width="20%">Pages</th>
                            <th>Cover</th>
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