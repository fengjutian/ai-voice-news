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
  Duration? _duration;
  double? _dragValue;

  @override
  void initState() {
    super.initState();
    _init();
  }

  Future<void> _init() async {
    try {
      final d = await _player.setUrl(
        'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3',
      );
      setState(() {
        _ready = true;
        _duration = d;
      });
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
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            if (_error != null)
              Text(_error!, style: const TextStyle(color: Colors.red)),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                ElevatedButton(
                  onPressed: _ready ? () => _player.play() : null,
                  child: const Text('播放'),
                ),
                const SizedBox(width: 12),
                ElevatedButton(
                  onPressed: _ready ? () => _player.pause() : null,
                  child: const Text('暂停'),
                ),
                const SizedBox(width: 12),
                ElevatedButton(
                  onPressed: _ready ? () => _player.stop() : null,
                  child: const Text('停止'),
                ),
              ],
            ),
            const SizedBox(height: 16),
            if (_duration != null)
              StreamBuilder<Duration>(
                stream: _player.positionStream,
                builder: (context, snapshot) {
                  final pos = snapshot.data ?? Duration.zero;
                  final max = _duration!.inMilliseconds.toDouble();
                  final value =
                      _dragValue ?? pos.inMilliseconds.toDouble().clamp(0, max);
                  return Column(
                    children: [
                      Slider(
                        min: 0,
                        max: max,
                        value: value,
                        onChanged: _ready
                            ? (v) => setState(() => _dragValue = v)
                            : null,
                        onChangeEnd: _ready
                            ? (v) {
                                _player.seek(Duration(milliseconds: v.round()));
                                setState(() => _dragValue = null);
                              }
                            : null,
                      ),
                      Text(
                        _formatDuration(Duration(milliseconds: value.round())) +
                            ' / ' +
                            _formatDuration(_duration!),
                      ),
                    ],
                  );
                },
              ),
          ],
        ),
      ),
    );
  }

  String _formatDuration(Duration d) {
    final two = (int n) => n.toString().padLeft(2, '0');
    final m = two(d.inMinutes.remainder(60));
    final s = two(d.inSeconds.remainder(60));
    final h = d.inHours;
    return h > 0 ? '$h:$m:$s' : '$m:$s';
  }
}
