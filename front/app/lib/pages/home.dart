import 'package:flutter/material.dart';
import 'package:just_audio/just_audio.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final AudioPlayer _player = AudioPlayer();
  bool _ready = false;
  String? _error;
  double _volume = 1.0;
  double _speed = 1.0;
  Duration? _duration;
  double? _dragValue;
  bool _shuffleEnabled = false;
  LoopMode _loopMode = LoopMode.off;
  final List<Map<String, String>> _tracks = [
    {
      'title': 'A Salute To Head-Scratching Science',
      'url': 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3',
    },
    {
      'title': 'Nature Sounds',
      'url': 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3',
    },
    {
      'title': 'From Cat Rheology To Operatic Incompetence',
      'url': 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3',
    },
  ];

  @override
  void initState() {
    super.initState();
    _init();
  }

  Future<void> _init() async {
    try {
      final sources = _tracks
          .map((t) => AudioSource.uri(Uri.parse(t['url']!)))
          .toList();
      final playlist = ConcatenatingAudioSource(
        children: sources,
        useLazyPreparation: true,
        shuffleOrder: DefaultShuffleOrder(),
      );
      await _player.setAudioSource(
        playlist,
        initialIndex: 0,
        initialPosition: Duration.zero,
      );
      setState(() => _ready = true);
    } catch (e) {
      setState(() => _error = e.toString());
    }
  }

  @override
  void dispose() {
    _player.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('首页')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            if (_error != null)
              Text(_error!, style: const TextStyle(color: Colors.red)),
            Container(
              height: 160,
              width: double.infinity,
              alignment: Alignment.center,
              decoration: BoxDecoration(
                color: Colors.grey.shade200,
                borderRadius: BorderRadius.circular(8),
              ),
              child: const Text('SCI FRI', style: TextStyle(fontSize: 28)),
            ),
            const SizedBox(height: 12),
            Text(
              _currentTitle,
              style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                const Icon(Icons.volume_up),
                Expanded(
                  child: Slider(
                    min: 0,
                    max: 1,
                    divisions: 10,
                    value: _volume,
                    onChanged: _ready
                        ? (v) {
                            setState(() => _volume = v);
                            _player.setVolume(v);
                          }
                        : null,
                  ),
                ),
                PopupMenuButton<double>(
                  initialValue: _speed,
                  onSelected: (v) {
                    setState(() => _speed = v);
                    _player.setSpeed(v);
                  },
                  itemBuilder: (context) => const [
                    PopupMenuItem(value: 0.75, child: Text('0.75x')),
                    PopupMenuItem(value: 1.0, child: Text('1.0x')),
                    PopupMenuItem(value: 1.25, child: Text('1.25x')),
                    PopupMenuItem(value: 1.5, child: Text('1.5x')),
                    PopupMenuItem(value: 2.0, child: Text('2.0x')),
                  ],
                  child: Text('${_speed.toStringAsFixed(2)}x'),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                IconButton(
                  icon: const Icon(Icons.skip_previous),
                  onPressed: _ready ? () => _player.seekToPrevious() : null,
                ),
                StreamBuilder<PlayerState>(
                  stream: _player.playerStateStream,
                  builder: (context, snapshot) {
                    final playing = snapshot.data?.playing ?? false;
                    return IconButton(
                      icon: Icon(playing ? Icons.pause : Icons.play_arrow),
                      onPressed: _ready
                          ? () => playing ? _player.pause() : _player.play()
                          : null,
                    );
                  },
                ),
                IconButton(
                  icon: const Icon(Icons.stop),
                  onPressed: _ready ? () => _player.stop() : null,
                ),
                IconButton(
                  icon: const Icon(Icons.skip_next),
                  onPressed: _ready ? () => _player.seekToNext() : null,
                ),
              ],
            ),
            const SizedBox(height: 8),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                ChoiceChip(
                  label: const Text('循环:关'),
                  selected: _loopMode == LoopMode.off,
                  onSelected: _ready
                      ? (_) {
                          setState(() => _loopMode = LoopMode.off);
                          _player.setLoopMode(LoopMode.off);
                        }
                      : null,
                ),
                const SizedBox(width: 8),
                ChoiceChip(
                  label: const Text('循环:列表'),
                  selected: _loopMode == LoopMode.all,
                  onSelected: _ready
                      ? (_) {
                          setState(() => _loopMode = LoopMode.all);
                          _player.setLoopMode(LoopMode.all);
                        }
                      : null,
                ),
                const SizedBox(width: 8),
                ChoiceChip(
                  label: const Text('循环:单曲'),
                  selected: _loopMode == LoopMode.one,
                  onSelected: _ready
                      ? (_) {
                          setState(() => _loopMode = LoopMode.one);
                          _player.setLoopMode(LoopMode.one);
                        }
                      : null,
                ),
                const SizedBox(width: 12),
                FilterChip(
                  label: const Text('随机'),
                  selected: _shuffleEnabled,
                  onSelected: _ready
                      ? (v) {
                          setState(() => _shuffleEnabled = v);
                          _player.setShuffleModeEnabled(v);
                        }
                      : null,
                ),
              ],
            ),
            const SizedBox(height: 12),
            StreamBuilder<Duration?>(
              stream: _player.durationStream,
              builder: (context, snapshot) {
                _duration = snapshot.data;
                return StreamBuilder<Duration>(
                  stream: _player.positionStream,
                  builder: (context, posSnap) {
                    final pos = posSnap.data ?? Duration.zero;
                    final max = (_duration ?? Duration.zero).inMilliseconds
                        .toDouble();
                    final value =
                        ((_dragValue ?? pos.inMilliseconds.toDouble()).clamp(
                          0,
                          max,
                        )).toDouble();
                    return Column(
                      children: [
                        Slider(
                          min: 0,
                          max: max == 0 ? 1.0 : max,
                          value: max == 0 ? 0.0 : value,
                          onChanged: _ready
                              ? (v) => setState(() => _dragValue = v)
                              : null,
                          onChangeEnd: _ready
                              ? (v) {
                                  _player.seek(
                                    Duration(milliseconds: v.round()),
                                  );
                                  setState(() => _dragValue = null);
                                }
                              : null,
                        ),
                        StreamBuilder<Duration>(
                          stream: _player.bufferedPositionStream,
                          builder: (context, bufSnap) {
                            final b = bufSnap.data ?? Duration.zero;
                            final bt = _formatDuration(b);
                            final ct = _formatDuration(
                              Duration(milliseconds: value.round()),
                            );
                            final dt = _formatDuration(
                              _duration ?? Duration.zero,
                            );
                            return Text('$ct / $dt  缓冲 $bt');
                          },
                        ),
                      ],
                    );
                  },
                );
              },
            ),
            const SizedBox(height: 12),
            Expanded(
              child: ListView.builder(
                itemCount: _tracks.length,
                itemBuilder: (context, i) {
                  final selected = i == (_player.currentIndex ?? 0);
                  return ListTile(
                    title: Text(_tracks[i]['title']!),
                    selected: selected,
                    onTap: _ready
                        ? () => _player.seek(Duration.zero, index: i)
                        : null,
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  String get _currentTitle {
    final i = _player.currentIndex ?? 0;
    return _tracks[i]['title']!;
  }

  String _two(int n) => n.toString().padLeft(2, '0');

  String _formatDuration(Duration d) {
    final m = _two(d.inMinutes.remainder(60));
    final s = _two(d.inSeconds.remainder(60));
    final h = d.inHours;
    return h > 0 ? '$h:$m:$s' : '$m:$s';
  }
}
