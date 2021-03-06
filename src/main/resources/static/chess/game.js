new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    data: () => ({

        stompClient: null,
        restCallsGoing: 0,

        pieceBox: { // https://commons.wikimedia.org/wiki/Category:SVG_chess_pieces
            true: {
                BP: 'https://upload.wikimedia.org/wikipedia/commons/4/49/CommonerB_Transparent.svg',
                BR: 'https://upload.wikimedia.org/wikipedia/commons/f/fa/Chess_Mdt45.svg',
                BN: 'https://upload.wikimedia.org/wikipedia/commons/c/c8/Chess_Udt45.svg',
                BB: 'https://upload.wikimedia.org/wikipedia/commons/9/98/Chess_bdt45.svg',
                BQ: 'https://upload.wikimedia.org/wikipedia/commons/c/cb/Chess_qdt26.svg',
                BK: 'https://upload.wikimedia.org/wikipedia/commons/f/f0/Chess_kdt45.svg',
                WP: 'https://upload.wikimedia.org/wikipedia/commons/7/7d/Commoner_Transparent.svg',
                WR: 'https://upload.wikimedia.org/wikipedia/commons/2/20/Chess_Mlt45.svg',
                WN: 'https://upload.wikimedia.org/wikipedia/commons/8/89/Chess_Ult45.svg',
                WB: 'https://upload.wikimedia.org/wikipedia/commons/5/50/Chess_tile_bl.svg',
                WQ: 'https://upload.wikimedia.org/wikipedia/commons/1/15/Chess_qlt45.svg',
                WK: 'https://upload.wikimedia.org/wikipedia/commons/4/42/Chess_klt45.svg'},
            false: {
                BP: 'https://upload.wikimedia.org/wikipedia/commons/c/c7/Chess_pdt45.svg',
                BR: 'https://upload.wikimedia.org/wikipedia/commons/f/ff/Chess_rdt45.svg',
                BN: 'https://upload.wikimedia.org/wikipedia/commons/e/ef/Chess_ndt45.svg',
                BB: 'https://upload.wikimedia.org/wikipedia/commons/9/98/Chess_bdt45.svg',
                BQ: 'https://upload.wikimedia.org/wikipedia/commons/4/47/Chess_qdt45.svg',
                BK: 'https://upload.wikimedia.org/wikipedia/commons/f/f0/Chess_kdt45.svg',
                WP: 'https://upload.wikimedia.org/wikipedia/commons/4/45/Chess_plt45.svg',
                WR: 'https://upload.wikimedia.org/wikipedia/commons/7/72/Chess_rlt45.svg',
                WN: 'https://upload.wikimedia.org/wikipedia/commons/7/70/Chess_nlt45.svg',
                WB: 'https://upload.wikimedia.org/wikipedia/commons/b/b1/Chess_blt45.svg',
                WQ: 'https://upload.wikimedia.org/wikipedia/commons/1/15/Chess_qlt45.svg',
                WK: 'https://upload.wikimedia.org/wikipedia/commons/4/42/Chess_klt45.svg'}},

        positions: {
            true: [
                ['H1', 'G1', 'F1', 'E1', 'D1', 'C1', 'B1', 'A1'],
                ['H2', 'G2', 'F2', 'E2', 'D2', 'C2', 'B2', 'A2'],
                ['H3', 'G3', 'F3', 'E3', 'D3', 'C3', 'B3', 'A3'],
                ['H4', 'G4', 'F4', 'E4', 'D4', 'C4', 'B4', 'A4'],
                ['H5', 'G5', 'F5', 'E5', 'D5', 'C5', 'B5', 'A5'],
                ['H6', 'G6', 'F6', 'E6', 'D6', 'C6', 'B6', 'A6'],
                ['H7', 'G7', 'F7', 'E7', 'D7', 'C7', 'B7', 'A7'],
                ['H8', 'G8', 'F8', 'E8', 'D8', 'C8', 'B8', 'A8']],
            false: [
                ['A8', 'B8', 'C8', 'D8', 'E8', 'F8', 'G8', 'H8'],
                ['A7', 'B7', 'C7', 'D7', 'E7', 'F7', 'G7', 'H7'],
                ['A6', 'B6', 'C6', 'D6', 'E6', 'F6', 'G6', 'H6'],
                ['A5', 'B5', 'C5', 'D5', 'E5', 'F5', 'G5', 'H5'],
                ['A4', 'B4', 'C4', 'D4', 'E4', 'F4', 'G4', 'H4'],
                ['A3', 'B3', 'C3', 'D3', 'E3', 'F3', 'G3', 'H3'],
                ['A2', 'B2', 'C2', 'D2', 'E2', 'F2', 'G2', 'H2'],
                ['A1', 'B1', 'C1', 'D1', 'E1', 'F1', 'G1', 'H1']]},

        board: {},
        capturedPieces: [],
        possibleMoves: [],
        promotionRequired: {
            W: false,
            B: false
        },

        dragStart: null,
        provisional: null,
        targets: [],

        alerts: [],
        newGameOptions: ['Standard', 'Parallel'],
        promotablePieces: 'NBRQ',

        frameSrc: '',

        whiteOnTop: false,
        outlandishPieces: false
    }),

    methods: {

        connect () {
            let socket = new SockJS('/chess-subscribe');

            // This may seem very contrived considering the player is in a plain text cookie; but it leaves the possibility open to use something less forgeable in the future
            this.axiosHelper('fetching state channel', () => axios.get('/chess-api/state-channel'), stateChannel => {

              this.stompClient = Stomp.over(socket);
              this.stompClient.connect({}, frame => {
                this.stompClient.subscribe('/chess-ws/' + stateChannel, this.receiveState);
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

        receiveState (message) {
            let state = JSON.parse(message.body);

            this.board = state.board;
            this.capturedPieces = state.capturedPieces;
            this.possibleMoves = state.possibleMoves;
//            this.provisional = state.provisional;
            this.promotionRequired.W = state.promotionRequiredW;
            this.promotionRequired.B = state.promotionRequiredB;

            this.dragStart = null;
            this.provisional = null;
            this.targets = [];
        },

        receiveAnnouncement (message) {
            this.alerts.push({type: 'info', text: message.body});
        },

        allowDrop(ev) {
            ev.preventDefault();
        },

        dragOrDrop(ev, position) {
          if (this.dragStart) {
            this.drop(ev, position);
          } else {
            this.drag(position);
          }
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

        save() {
            this.restCallsGoing++;
            // TODO Errors from this call (e.g. no game in progress) don't make it to the UI
            this.frameSrc = '/chess-api/save';
            setTimeout(() => {
               this.frameSrc = '';
               this.restCallsGoing--;
            }, 3000);
        },

        triggerLoad() {
            this.$refs.chessLoadFileInput.click();
        },

        load() {
            let selectedFile = this.$refs.chessLoadFileInput.files.length && this.$refs.chessLoadFileInput.files[0];
            if (selectedFile) {
                let formData = new FormData();
                formData.set("file", selectedFile);

                this.axiosHelper('loading game data', () => axios.post('/chess-api/load', formData, {
                     headers: { "Content-Type": "multipart/form-data" }}));
            }
        },

        newGame(option) {
            this.axiosHelper('calling for new game', () => axios.post('/chess-api/new/' + option.toLowerCase(), {}));
        },

        promote(color, piece) {
            // Manually setting content type because a single string isn't JSON enough to be recognised
            this.axiosHelper('sending promotion choice', () => axios.post('/chess-api/promote', '"' + color + piece + '"', {headers: {'Content-Type': 'application/json'}}));
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
