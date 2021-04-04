new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    data: () => ({

        stompClient: null,
        restCallsGoing: 0,

        pieceBox: {
            // https://commons.wikimedia.org/wiki/Category:SVG_chess_pieces
            BP: 'https://upload.wikimedia.org/wikipedia/commons/4/49/CommonerB_Transparent.svg',
            BR: 'https://upload.wikimedia.org/wikipedia/commons/f/ff/Chess_rdt45.svg',
            BN: 'https://upload.wikimedia.org/wikipedia/commons/c/c8/Chess_Udt45.svg',
            BB: 'https://upload.wikimedia.org/wikipedia/commons/9/98/Chess_bdt45.svg',
            BQ: 'https://upload.wikimedia.org/wikipedia/commons/4/47/Chess_qdt45.svg',
            BK: 'https://upload.wikimedia.org/wikipedia/commons/f/f0/Chess_kdt45.svg',
            WP: 'https://upload.wikimedia.org/wikipedia/commons/7/7d/Commoner_Transparent.svg',
            WR: 'https://upload.wikimedia.org/wikipedia/commons/7/72/Chess_rlt45.svg',
            WN: 'https://upload.wikimedia.org/wikipedia/commons/8/89/Chess_Ult45.svg',
            WB: 'https://upload.wikimedia.org/wikipedia/commons/b/b1/Chess_blt45.svg',
            WQ: 'https://upload.wikimedia.org/wikipedia/commons/1/15/Chess_qlt45.svg',
            WK: 'https://upload.wikimedia.org/wikipedia/commons/4/42/Chess_klt45.svg'
        },
        positions: [
            ['A8', 'B8', 'C8', 'D8', 'E8', 'F8', 'G8', 'H8'],
            ['A7', 'B7', 'C7', 'D7', 'E7', 'F7', 'G7', 'H7'],
            ['A6', 'B6', 'C6', 'D6', 'E6', 'F6', 'G6', 'H6'],
            ['A5', 'B5', 'C5', 'D5', 'E5', 'F5', 'G5', 'H5'],
            ['A4', 'B4', 'C4', 'D4', 'E4', 'F4', 'G4', 'H4'],
            ['A3', 'B3', 'C3', 'D3', 'E3', 'F3', 'G3', 'H3'],
            ['A2', 'B2', 'C2', 'D2', 'E2', 'F2', 'G2', 'H2'],
            ['A1', 'B1', 'C1', 'D1', 'E1', 'F1', 'G1', 'H1']
        ],

        board: {},
        capturedPieces: [],
        possibleMoves: [],

        dragStart: null,
        provisional: null,
        targets: [],

        alerts: [],
// TODO See the TODO in game.html
//        newGameOptions: ['Standard', 'Turnless'],
//        newGameSelection: null,

        whiteOnTop: false,
        outlandishPieces: false
    }),

    methods: {

        connect () {
            let socket = new SockJS('/chess-subscribe');

            // This may seem very contrived considering the player is in a plain text cookie; but it leaves the possibility open to use something less forgeable in the future
            this.axiosHelper('fetching moves channel', () => axios.get('/chess-api/moves-channel'), possibleMovesChannel => {

              this.stompClient = Stomp.over(socket);
              this.stompClient.connect({}, frame => {
                this.stompClient.subscribe('/chess-ws/board', this.receiveBoard);
                this.stompClient.subscribe('/chess-ws/captured-pieces', this.receiveCapturedPieces);
                this.stompClient.subscribe('/chess-ws/' + possibleMovesChannel, this.receivePossibleMoves);
                this.stompClient.subscribe('/chess-ws/announcements', this.receiveAnnouncement);
              });

              // Apparently this is how you stop the debug log
              this.stompClient.debug = null;

              // Poor man's callback chain
              let stompOnClose = socket.onclose;
              socket.onclose = status => {
                  stompOnClose(status);
                  this.stompClient = null;
              }
            });
        },

        receiveBoard (message) {
            this.board = JSON.parse(message.body);
        },

        receiveCapturedPieces (message) {
            this.capturedPieces = JSON.parse(message.body);
        },

        receivePossibleMoves (message) {
            this.possibleMoves = JSON.parse(message.body);
            this.dragStart = null;
            this.provisional = null;
            this.targets = [];
        },

        receiveAnnouncement (text) {
            this.alerts.push({type: 'info', text});
        },

        allowDrop(ev) {
            ev.preventDefault();
        },

        drag(position) {
            this.dragStart = position;
            this.targets = this.possibleMoves.filter(move => move[0] === position).map(move => move[1]);
        },

        drop(ev, position) {
            ev.preventDefault();

            if (this.targets.indexOf(position) > -1) {
                this.axiosHelper('sending move', () => axios.post('/chess-api/move', [this.dragStart, position]));
                this.provisional = position
                this.board[position] = this.board[this.dragStart];
                this.$forceUpdate();
            }

            this.dragStart = null;
            this.targets = [];
        },

        newStandardGame () {
            this.axiosHelper('calling for new game', () => axios.post('/chess-api/new/standard', {}));
        },

        axiosHelper(flowDescription, promiseProducer, dataConsumer) {
            // dataConsumer is optional... because I said so
            if (!dataConsumer)
              return this.axiosHelper(flowDescription, promiseProducer, () => {});

            this.restCallsGoing++;
            promiseProducer()
              .then(response => dataConsumer(response.data))
              .catch(error => {
                let errorLog = 'Error ' + flowDescription;
                console.log(errorLog, error);
                let errorCode = error.response && error.response.status && ' (' + error.response.status + ')' || '';
                let errorDetail = error.response && error.response.data || error.toString();
                this.alerts.push({type: 'warning', text: errorLog + errorCode + ': ' + errorDetail});
              })
              .then(() => this.restCallsGoing--);
        }
    },

    mounted () {
        this.connect();
    }
});