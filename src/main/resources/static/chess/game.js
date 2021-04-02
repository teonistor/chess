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
            this.restCallsGoing++;
            let socket = new SockJS('/chess-subscribe');

            // This may seem very contrived considering the player is in a plain text cookie; but it leaves the possibility open to use something less forgeable in the future
            axios.get('/chess-api/moves-channel')
              .then(response => {
                console.log(response);
                let possibleMovesChannel = response.data;
                this.restCallsGoing--;

                this.stompClient = Stomp.over(socket);
                this.stompClient.connect({}, frame => {
                  console.log("Subscribing to board", this.receiveBoard)
                  this.stompClient.subscribe('/chess-ws/board', this.receiveBoard);
                  console.log("Subscribing to captured-pieces")
                  this.stompClient.subscribe('/chess-ws/captured-pieces', this.receiveCapturedPieces);
                  console.log("Subscribing to " + possibleMovesChannel)
                  this.stompClient.subscribe('/chess-ws/' + possibleMovesChannel, this.receivePossibleMoves);
                  console.log("Subscribing to announcements")
                  this.stompClient.subscribe('/chess-ws/announcements', this.receiveAnnouncement);
                  console.log("Subscribed all")
                });

              // Poor man's callback chain
              let stompOnClose = socket.onclose;
              socket.onclose = status => {
                  stompOnClose(status);
                  this.stompClient = null;
              }

            })
             .catch(error => {
                console.log(error);
                this.restCallsGoing--;
                this.alerts.push({type: 'warning', text: 'Error fetching moves channel: ' + error});
            });
        },

        receiveBoard (message) {
        console.log('receiveBoard', message)
            this.board = JSON.parse(message.body);
        },

        receiveCapturedPieces (message) {
        console.log('receiveCapturedPieces', message)
            this.capturedPieces = JSON.parse(message.body);
        },

        receivePossibleMoves (message) {
        console.log('receivePossibleMoves', message)
            this.possibleMoves = JSON.parse(message.body);
            this.dragStart = null;
            this.provisional = null;
            this.targets = [];
        },

        receiveAnnouncement (message) {
        console.log('receiveAnnouncement', message)
            // TODO
        },


        restart() {
            // this.stompClient.send("/ttt/restart", {}, ".");
            // this.winner = '';
        },


        send (i, j) {
            this.stompClient.send("/chess-ws/move", {}, JSON.stringify([this.dragStart]));
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
                this.provisional = position
                this.board[position] = this.board[this.dragStart];
                this.$forceUpdate();

                axios.post('/chess-api/move', [this.dragStart, position])
                    // .then(function (response) {
                    //   console.log(response);
                    // })
                    .catch(function (error) {
                        console.log(error);
                        this.alerts.push({type: 'warning', text: 'Error sending move: ' + error});
                    });
                // console.log(axios({method: 'POST', url: this.sourcePath, data: [this.dragStart, position]}))
                // .then(result => {
                //       this.entryHtml = result.data;
                //     },
                //     error => {
                //       this.canLoad = true;
                //       this.entryHtml = ' ';
                //       console.error(error);
                //       switch (error.response && error.response.status) {
                //           // TODO these are specific to one use case; and we probably want a more global error balloon framework anyhow
                //         case 401:
                //           this.errorMessage = 'You do not have access to read this entry.';
                //           break;
                //         case 404:
                //           this.errorMessage = 'Entry not found. The table of contents is probably broken.';
                //           break;
                //         default :
                //           this.errorMessage = 'Could not load entry: ' + error.toString();
                //           break;
                //       }
                //     });
            }

            this.dragStart = null;
            this.targets = [];
        },

        newStandardGame () {
          this.restCallsGoing++;

          let url = '/chess-api/new/standard';
          console.log("New game  " + url, this.restCallsGoing)

          axios.post(url, {})
            .then(response => {
              this.restCallsGoing--;
            })
            .catch(error => {
              this.restCallsGoing--;
              console.log('Error calling for new game:', error);
              this.alerts.push({type: 'warning', text: 'Error calling for new game: ' + error});
            });
        }
    },

    mounted () {
        this.connect();
    }
});
