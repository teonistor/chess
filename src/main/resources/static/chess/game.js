new Vue({
  el: '#app',
  // vuetify: new Vuetify(),
  data: () => ({

    stompClient: null,

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
    provisional: [],
    targets: [],

    whiteOnTop: false,
    outlandishPieces: false
  }),

  methods: {

    connect () {
      let socket = new SockJS('/chess-subscribe');
      this.stompClient = Stomp.over(socket);
      this.stompClient.connect({}, frame => {
        this.stompClient.subscribe('/chess-ws/board', this.receiveBoard);
        this.stompClient.subscribe('/chess-ws/announcements', this.receiveAnnouncement);
      });

      // Poor man's callback chain
      let stompOnClose = socket.onclose;
      socket.onclose = status => {
        stompOnClose(status);
        this.stompClient = null;
      }
    },

    receiveBoard (message) {
      console.log('receive board', message)
      let data = JSON.parse(message.body);
      this.board = data[0];
      this.capturedPieces = data[1];
      this.possibleMoves = data[2];
    },

    receiveAnnouncement (message) {
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
    },

    drop(ev, position) {
      ev.preventDefault();
      this.provisional.push(position)
      this.board[position] = this.board[this.dragStart];
      this.$forceUpdate();
    }
  },

  mounted () {
    this.connect();
  }
});
